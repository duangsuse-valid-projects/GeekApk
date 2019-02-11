require './spectrum'
require 'time'

class String
  def escape_uri
    return URI.encode(self)
  end

  def to_time
    return Time.parse(self)
  end
end

class Spectrum::ClientShowcase
  def self.map_resp_text_number(t, _, _me); t.to_i; end
end

class Times
  attr_accessor :spectrum, :posts, :user, :pass

  class PostReader
    def [](nth)
      Post.new(@conn, @conn.readPost(nth), nth, @times.user)
    end

    def preview(nth)
      Post.new(@conn, @conn.readPostPreview(nth), nth, @times.user)
    end

    def views(nth)
      @conn.readPostViews(nth)
    end

    def write(title, content = ' ', author = @times.user)
      @conn.makePost(author.escape_uri, @times.pass, title.escape_uri, content)
    end

    def shift
      @conn.freeLastPost(@times.pass)
    end

    def size
      @conn.countPosts
    end

    def initialize(ds, times)
      @conn = ds
      @times = times
    end
  end

  class Post
    attr_accessor :ds, :id, :o, :user

    def initialize(ds, o, id, user)
      @ds = ds; @id = id; @o = o; @user = user
    end

    def inspect; @o; end

    def as_detail; Post.new(ds, ds.readPost(id), id, user); end
    def comments; ds.readPostComments(id).map { |j| Comment.new(ds, j, id, j['created'].to_time) }; end
    def count_comments; ds.countPostComment(id); end
    def write_comment(content, author = @user); ds.writePostComment(id, author.escape_uri, content); end
  end

  class Comment
    attr_accessor :ds, :tid, :ctime, :o

    def initialize(ds, o, tid, ctime)
      @ds = ds; @o = o
      @tid = tid; @ctime = ctime
    end

    def inspect; @o; end

    def like!; ds.likePostComment(@tid, @ctime.to_i); end
  end

  def initialize(url = 'http://0.0.0.0:234', user: 'foo', pass: 'dolphins')
    @spectrum = if File.exist?('spectrum_times.json')
      Spectrum::ClientShowcase.from_file('spectrum_times.json')
    else Spectrum::ClientShowcase.from_file("#{__dir__}/times.geekspec") end
    @spectrum.conn.url_prefix = url
    @spectrum.instance_api_methods
    @posts = PostReader.new(@spectrum, self)
    @user = user
    @pass = pass.escape_uri
  end

  def version; spectrum.apiVersion; end
  def posts_count; spectrum.countPosts; end
  def stream; spectrum.realtime; end

  def all_posts
    ary = []
    index = 0
    while index <= posts_count - 1
      ary << posts[index]
      index += 1
    end
    return ary
  end
end
