require './spec/spec_helper'

RSpec.describe Times do
  it 'has a version number' do
    expect(Times::VERSION).not_to be nil
  end

  it 'does something useful' do
    expect(false).to eq(false)
  end

  describe Times::Comment do
    it 'can new' do
      c = Times::Comment.new('duangsuse', 'hello')
      expect(c).not_to be nil
    end

    it 'have attr accessors and can like' do
      c = Times::Comment.new('fool', 'who am i???')
      expect(c.author).to eq 'fool'
      expect(c.text).to eq 'who am i???'
      expect(c.likes).to eq 0
      c.like
      expect(c.likes).to eq 1
      c.likes = 50
      expect(Time.now - c.created).not_to be nil
    end

    it 'serializes&deserialize right' do
      c = Times::Comment.new('boy', 'Oh, my shoulder')
      c.likes = 1
      c.created = Time.at 2333
      ser = c.ser
      expect(JSON.parse(ser.to_json)).to eq({
        'author' => 'boy',
        'text' => 'Oh, my shoulder',
        'created' => Time.at(2333).to_s,
        'likes' => 1
      })
      expect(Times::Comment.de(JSON.parse ser.to_json).ser).to eq(c.ser)
      c.text << '\n' << 'Billy replies: Oh f**king c**ming'
      expect(c.ser[:text]).to include('Billy')
    end
  end

  describe Times::Post do
    it 'can new' do
      sum = 'After a big py trade, Gee Android client released at 2018.3.10'
      p = Times::Post.new 'duangsuse', 'Gee v0.1.0 released', sum, 'rt'
      expect(p.author).to eq 'duangsuse'
      expect(p.title).to eq 'Gee v0.1.0 released'
      expect(p.summary).to eq sum
      expect(p.text).to eq 'rt'
      expect(p.created.class).to eq Time
      expect(p.comments.class).to eq Array
      expect(p.comments.size).to eq 0
      expect(p.views).to eq 0
    end

    it 'serializes&deserialize right' do
      p = Times::Post.new 'billy', 'Ass we can', '**********', '(nsfw content)'
      p.created = Time.new '1926.8.17'
      ser = p.ser
      expect(JSON.parse(ser.to_json)).to eq({
        'author' => 'billy',
        'title' => 'Ass we can',
        'summary' => '**********',
        'text' => '(nsfw content)',
        'views' => 0,
        'comments' => [],
        'created' => Time.new('1926.8.17').to_s
      })
      de = Times::Post.de(JSON.parse(ser.to_json))
      expect(de.ser).to eq(p.ser)

      p.comment('duangsuse', ':see_no_evil:')
      p.comment('van', 'f**k you')

      expect(Times::Post.de(JSON.parse(p.ser.to_json)).ser[:text]).to eq(p.ser[:text])
      expect(Times::Comment.de(JSON.parse(p.ser.to_json)['comments'][0]).author).to eq('duangsuse')
      expect(Times::Comment.de(JSON.parse(p.ser.to_json)['comments'][0]).text).to eq(':see_no_evil:')
    end

    it 'implemented ser_web right' do
      p = Times::Post.new 'ivanilla', 'A', 'B', 'C'
      p.views = 2
      p.created = Time.at 666
      expect(JSON.parse(p.ser_web)).to eq({
        'author' => 'ivanilla',
        'title' => 'A',
        'summary' => 'B',
        'text' => 'C',
        'created' => Time.at(666).to_s,
        'views' => 2
      })
    end

    it 'implemented ser_short right' do
      p = Times::Post.new 'trumeet', 'bad rikka', 'bad android', 'bad geekapk'
      p.created = Time.at 0
      expect(JSON.parse(p.ser_short)).to eq({
        'author' => 'trumeet',
        'title' => 'bad rikka',
        'summary' => 'bad android',
        'created' => Time.at(0).to_s
      })
    end

    it 'implements helper function right' do
      p = Times::Post.new 'pandecheng', 'Emmmm', '233', '#(Funny)'
      p.add_view
      expect(p.views).to eq(1)
      expect(p.comment('a', '')).to eq(1)

      b = Times::Comment.new 'B', 'Not B'
      b.created = Time.at(2).to_i
      p.comments << b
      p.like Time.at(2).to_i
      expect(p.comments[1].likes).to eq(1)

      expect(p.view).to eq(p.ser_web)
      expect(p.views).to eq(2)
    end
  end

  # Clear testing storage
  it 'pruning posts' do
    Times.instance_variable_set(:@posts, [])
  end

  it 'stores posts' do
    Times.posts << Times::Post.new('', '', '', '')
    expect(Times.posts.size).to eq(1)
  end

  it 'pops post' do
    Times.pop
    expect(Times.posts.size).to eq(0)
  end

  it 'adds post' do
    expect(Times.add_post(Times::Post.new('', '', '', ''))).to eq(1)
    expect(Times.posts.size).to eq(1)
  end

  it 'serializes well' do
    p0 = Times::Post.new 'pandecheng', 'Emmmm', '233', '#(Funny)'
    p0.comment('duangsuse', ':+1:')
    p0.comment('doge', 'emmmm')
    p0.comment('dbin_k', '(')
    p1 = Times::Post.new 'trumeet', 'bad rikka', 'bad android', 'bad geekapk'
    p1.comment('neofelhz', ')')
    p2 = Times::Post.new 'billy', 'Ass we can', '**********', '(nsfw content)'
    p2.comment('lwl12', '233')
    p3 = Times::Post.new 'billy', 'Ass we can', '**********', '(nsfw content)'
    [p0, p1, p2, p3].each { |p| Times.add_post(p) }
    ser = Times.ser
    expect(JSON.parse(ser)[4]['author']).to eq('billy')
    expect(JSON.parse(ser).size).to eq(5)
  end

  it 'deserialize well' do
    ser = Times.ser
    de = Times.de(ser)
    4.times { |n| expect(Times.posts[n].instance_variables).to eq(de[n].instance_variables) }
  end

  it 'can dump' do
    Times.dump
    expect(File.exist?('posts.json')).to eq(true)
    expect(File.read('posts.json')).to include('billy')
  end

  it 'can load' do
    expect(Times.posts.size).to eq(5)
    Times.add_post Times.posts[2]
    expect(Times.posts.size).to eq(6)
    Times.load
    expect(Times.posts.size).to eq(5)
  end

  it 'cleaning environment' do
    File.delete 'posts.json'
  end
end
