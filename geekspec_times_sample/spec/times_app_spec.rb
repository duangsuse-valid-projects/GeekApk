require './spec/spec_helper'

RSpec.describe TimesApp do
  it 'did this test' do
    expect(1).to eq(1)
  end

  it 'has password' do
    expect(TimesApp::TRB_PASS).not_to be(nil)
  end

  it 'renders /' do
    get '/'
    expect(last_response).to be_ok
    Times.add_post Times::Post.new('duangsuse', 'got tired', 'hmm...', '(none)')
    get '/'
    expect(last_response.body).to include('duangsuse')
    expect(last_response.body).not_to include('(none')
  end

  it 'renders /:post' do
    expect(Times.posts[0]).not_to be(nil)
    get '/0'
    expect(last_response).to be_ok
    expect(Times.posts[0].views).to eq(1)
    expect(last_response.body).to include('(none)')
  end

  it 'renders /:post/comments' do
  Times.posts[0].comment('fly3949', 'emmmm') # (((
    get '/0/comments'
    expect(last_response).to be_ok
    expect(last_response.body).to include('fly3949')
  end

  it 'renders /adm' do
    get '/adm'
    expect(last_response).to be_ok
  end

  it 'pruning spam log' do
    TimesApp::ANTI_SPAM.delete '127.0.0.1'
  end

  it 'stops spam' do
    a = TimesApp.anti_spam
    expect(a).to eq({})
    a = TimesApp.anti_spam
    class TimesApp
      client_down(1, '1')
    end
    expect(a.size).to eq(1)
    expect(a['1']).to eq(1)
    class TimesApp
      client_down(500, '1')
    end
    ta = TimesApp.allocate
    m = TimesApp.instance_method :client_banned?
    m.bind(ta)
    expect(ta.client_banned?('1')).to be(true)
  end

  describe 'APIs' do
    it 'gives version' do
      get '/api/version'
      expect(last_response.body).to eq(Times::VERSION)
    end

    it 'gives length' do
      get '/api'
      expect(last_response.body).to eq(Times.posts.size.to_s)
    end

    it 'gives post' do
      a = Times::Post.new('duangsuse_dup', 'got tired', 'hmm...', '(none)')
      a.comment('233', 'QWQ')
      Times.add_post a
      expect(Times.posts.size).to eq(2)
      get '/api/1'
      expect(last_response.body).to include('duangsuse_dup')
      get '/api/0'
      expect(last_response.body).to include('duangsuse')
      expect(last_response.body).not_to include('(none)')
      expect(last_response.body).not_to include('QWQ')
    end

    it 'gives detail' do
      get '/api/0/detail'
      expect(last_response.body).to include('(none)')
    end

    it 'gives views' do
      get '/api/0/views'
      expect(last_response.body).to eq(Times.posts[0].views.to_s)
    end

    it 'gives comments' do
      get '/api/1/comments'
      expect(last_response.body).to include('QWQ')
      expect(JSON.parse(last_response.body)[0]['author']).to eq('233')
    end

    it 'adds comment' do
      post '/api/1/comment/duangsuse', 'Hello XD'
      get '/api/1/comments'
      expect(last_response).to be_ok
      expect(last_response.body).to include('Hello XD')
      expect(JSON.parse(last_response.body)[1]['author']).to eq('duangsuse')
    end

    it 'likes comments' do
      ct = Times.posts[1].comments[0].created.to_i
      post "/api/1/comment/#{ct}/like"
      expect(last_response).to be_ok
      get '/api/1/comments'
      expect(last_response).to be_ok
      expect(last_response.body).to include('likes":1')
    end

    it 'serves comment length' do
      get '/api/1/comments/len'
      expect(last_response).to be_ok
      expect(last_response.body).to eq('2')
    end

    it 'can pop post' do
      get '/api'
      expect(last_response.body).to eq('2')
      delete "/api/pop/#{TimesApp::TRB_PASS}"
      expect(last_response).to be_ok
      expect(last_response.body).to eq('got tired')
      get '/api'
      expect(last_response.body).to eq('1')
      delete "/api/pop/#{TimesApp::TRB_PASS}"
      expect(last_response).to be_ok
      expect(Times.posts.size).to eq(0)
      expect(last_response.body).to eq('got tired')
      delete "/api/pop/#{TimesApp::TRB_PASS}"
      expect(last_response.body).to include(':-(')
      expect(last_response.status).to eq(410)
    end

    it 'can post' do
      title = URI.encode_www_form ['An old frog killed a young man']
      post "/api/duangsuse/#{TimesApp::TRB_PASS}/#{title}", "2018 +1s\n rt"
      # File.write 'a.html', last_response.body
      expect(last_response).to be_ok
      a = Times.posts.pop
      expect(a.title).to eq(title)
      expect(a.summary).not_to include('rt')
      expect(a.summary).to include('1s')
      expect(a.text).to include('+1s')
    end

    describe 'Realtime API' do
      it 'rejects closed' do
        get '/api/realtime'
        expect(TimesApp::CONNECTIONS.size).to eq(0)
      end

      # bad useless duangsuse(
      it 'continues going' do
        require 'net/http'
        # conn = Net::HTTP.new('127.0.0.1', TimesApp.port)
        # req = Net::HTTP::Get.new('/api/realtime')
        # conn.request(req) do |res|
        #   res.read_body do |data|
        #     expect(TimesApp.instance_variable_get(:@connections).size).to eq(1)
        #   end
        # end
      end
    end
  end

  it 'dump on exit' do
    pid = Process.pid
    $ta_test = 0
    module Times
      class << self
        alias old_dump dump
      end
    end
    def Times.dump
      $ta_test += 1
      old_dump
    end
    $not_exit = true
    %i[QUIT TERM INT USR1].each do |s|
      Process.kill s, pid
    end
    $not_exit = false
    expect($ta_test).to eq(4)
  end

  it 'clears spamming log' do
    pid = Process.pid
    TimesApp::ANTI_SPAM.clear
    TimesApp::ANTI_SPAM['12.32.43.1'] = 30
    TimesApp::ANTI_SPAM['212.3.42.4'] = 20
    Process.kill :USR2, pid
    expect(TimesApp.anti_spam).to eq({})
    expect(File.read('spam_log')).to eq("{\"12.32.43.1\"=>30, \"212.3.42.4\"=>20}\n")
    File.delete 'spam_log'
  end
end
