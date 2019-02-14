require 'json'
require 'sinatra'
require 'erb'

Encoding.default_internal = Encoding::UTF_8
Encoding.default_external = Encoding::UTF_8

# Times Server
module Times
  VERSION = '0.1.0'.freeze

  @posts = []
  @storage = ENV['TRB_STORAGE'] || 'posts.json'

  # Comment in post
  class Comment
    def initialize(author, text)
      @author = author
      @text = text
      @created = Time.now
      @likes = 0
    end

    # Serialize comment
    def ser
      {
        author: @author,
        text: @text,
        created: @created,
        likes: @likes
      }
    end

    # New a comment from json object
    def self.de(j)
      ret = Comment.new j['author'], j['text']
      ret.created = Time.parse j['created']
      ret.likes = j['likes']
      ret
    end

    # like a comment
    def like
      @likes += 1
    end

    attr_accessor :author, :text, :created, :likes
  end

  # News item
  class Post
    def initialize(author, title, summary, text)
      @author = author
      @title = title
      @summary = summary
      @text = text
      @created = Time.now
      @views = 0
      @comments = []
    end

    # Comment this post
    def comment(author, text)
      @comments << Comment.new(author, text)
      @comments.size
    end

    # Serialize post comments
    def ser_comments
      @comments.map { |c| c.ser }.to_json
    end

    # Serialize self
    def ser
      {
        author: @author,
        title: @title,
        summary: @summary,
        text: @text,
        created: @created,
        views: @views,
        comments: @comments.map { |c| c.ser }
      }
    end

    def ser_web
      {
        author: @author,
        title: @title,
        summary: @summary,
        text: @text,
        created: @created,
        views: @views
      }.to_json
    end

    # Create a post from json object
    def self.de(j)
      ret = Post.new j['author'], j['title'], j['summary'], (j['text'] || '')
      ret.created = Time.parse j['created']
      ret.views = j['views'] || 0
      ret.comments = j['comments'].map { |c| Comment.de c }
      ret
    end

    # view ++
    def add_view
      @views += 1
    end

    ## Sort serialize for web api
    def ser_short
      {
        author: @author,
        title: @title,
        summary: @summary,
        created: @created
      }.to_json
    end

    # Like a comment
    def like(d)
      @comments.each do |c|
        c.like if c.created.to_i == d
      end
    end

    # View post detail
    def view
      add_view
      ser_web
    end

    attr_accessor :author, :title, :summary, :text, :created, :views, :comments
  end

  # remove a post
  def self.pop
    @posts.pop
  end

  # add a post
  def self.add_post(post)
    @posts << post
    @posts.size
  end

  # Serialize posts
  def self.ser
    posts = []
    @posts.each { |p| posts << p.ser }
    posts.to_json
  end

  # Deserialize posts
  def self.de(json)
    j = JSON.parse json
    tmp = []
    j.map { |p| tmp << Post.de(p) }
    tmp
  end

  # Dump to file
  def self.dump
    File.write @storage, ser
  end

  # Load from file
  def self.load
    @posts = de File.read(@storage)
  end

  def self.posts
    Times.instance_variable_get :@posts
  end
end

# Web handler
class TimesApp < Sinatra::Base
  set logging: ENV['TRB_LOG'] == 'yes'
  set bind: ENV['TRB_BIND'] || '0.0.0.0'
  set port: ENV['TRB_PORT'] || '234'

  TRB_PASS = ENV['TRB_PASS'] || 'dolphins'
  puts "Password set to #{TRB_PASS}"
  CONNECTIONS = Set.new
  # I'm really sorry...
  ANTI_SPAM = {}

  def self.anti_spam
    ANTI_SPAM
  end

  def client_banned?(ip)
    return false unless TimesApp.anti_spam.key?(ip)
    TimesApp.anti_spam[ip] > 500
  end

  def client_ip(req)
    req.ip
  end

  class << self
    def client_down(n, k)
      TimesApp.anti_spam[k] = 0 unless TimesApp.anti_spam.key? k
      TimesApp.anti_spam[k] += n
    end
  end

  # Returns times version
  get '/api/version' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(1, request.ip)
    Times::VERSION
  end

  # Returns News array length
  get '/api' do
    Times.posts.size.to_s
  end

  # Returns blog preview
  get '/api/:nth' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(3, request.ip)
    nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    Times.posts[nth].ser_short
  end

  # Returns blog post
  get '/api/:nth/detail' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(3, request.ip)
    nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    Times.posts[nth].view
  end

  # Returns blog post views
  get '/api/:nth/views' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(1, request.ip)
    nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    Times.posts[nth].views.to_s
  end

  # Returns blog comments
  get '/api/:nth/comments' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(3, request.ip)
    nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    Times.posts[nth].ser_comments
  end

  # Add a comment
  post '/api/:nth/comment/:author' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(10, request.ip)
    author = URI.decode(params['author'])
    # halt 400, 'Bad Encoding' unless author.encoding == Encoding::UTF_8
    nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    body = URI.decode(request.body.read)
    # halt 400, 'Bad Body Encoding' unless body.encoding == Encoding::UTF_8
    Times.posts[nth].comment(author, body.force_encoding('UTF-8'))
    logger.info "#{author} added a comment to #{nth}"
    CONNECTIONS.each do |o|
      o << 'c' << params['nth'] << ';'
    end
    'ok'
  end

  # Like a comment
  post '/api/:nth/comment/:ctime/like' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(5, request.ip)
    nth = params['nth'].to_i
    ctime = params['ctime'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    return 'ok' if Times.posts[nth].like(ctime)
    halt 500, 'err'
  end

  # Returns comment length
  get '/api/:nth/comments/len' do
    nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > nth && (nth.positive? || nth.zero?)
    Times.posts[nth].comments.size.to_s
  end

  # Removes latest post
  delete '/api/pop/:pass' do
    halt 401, 'Bad Auth' unless params['pass'] == TRB_PASS
    p = Times.pop
    halt 410, ':-( Nothing to pop' if p.nil?
    logger.info "Post poped: #{p.title}"
    return p.title
  end

  # Posts a new post
  post '/api/:author/:pass/:title' do
    halt 401, 'Bad Auth' unless params['pass'] == TRB_PASS
    author = URI.decode(params['author'])
    # puts author.encoding
    # halt 400, 'Bad Author Encoding' unless author.encoding == Encoding::UTF_8
    title = URI.decode(params['title'])
    body = URI.decode(request.body.read.force_encoding('UTF-8'))
    # halt 400, 'Body Encoding Must be UTF-8' unless body.encoding == Encoding::UTF_8
    halt 400, 'Blank posts not allowed' if body.empty?
    summary = body.lines.first
    post = Times::Post.new author, title, summary, body
    Times.add_post post
    return 'ok' if Times.posts.last.title == title

    CONNECTIONS.each { |pipe| pipe << 'r' << (Times.posts.size - 1) << ';' }

    halt 500, 'Unknown error((('
  end

  # Register a update stream
  get '/api/realtime' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(2, request.ip)
    return stream(:keep_open) do |out|
      CONNECTIONS << out
      CONNECTIONS.reject!(&:closed?)
    end
  end

  # Views
  get '/' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(2, request.ip)
    @posts = Times.posts
    erb :index
  end

  get '/:nth' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(1, request.ip)
    @nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > @nth && (@nth.positive? || @nth.zero?)
    @post = Times.posts[@nth]
    erb :post
  end

  get '/:nth/comments' do
    halt 423, 'Banned' if client_banned?(client_ip(request))
    TimesApp.client_down(2, request.ip)
    @nth = params['nth'].to_i
    halt 400, 'Bad Request' unless Times.posts.size > @nth && (@nth.positive? || @nth.zero?)
    @post = Times.posts[@nth]
    erb :comments
  end

  get '/adm/:pass' do
    halt 401, 'Bad Auth' unless params['pass'] == TRB_PASS
    erb :admin
  end
end

# Starts the application, and load posts if found
Times.load if File.exist? Times.instance_variable_get :@storage
TimesApp.run

# Normal exit application
def app_exit
  puts ';-) Bye-bye' unless $not_exit
  Times.dump
  exit unless $not_exit
end

# Trap quit signals
trap(:QUIT) { app_exit }
trap(:TERM) { app_exit }
trap(:INT) { app_exit }
trap(:USR1) { Times.dump }

trap :USR2 do
  puts 'Cleaning spam log...' unless ENV['RACK_ENV'] == 'test'
  l = TimesApp::ANTI_SPAM
  f = File.open 'spam_log', 'w+'
  f.puts l
  f.close
  l.clear
end
