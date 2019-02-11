#!env -S ruby -W2 -w -v -E UTF-8 -T0

# Spectrum - a ruby HTTP client program paired with GeekSpec DSL

# GeekApk - https://github.com/duangsuse/GeekApk
#
# Write your API specifications with GeekSpec markup and load them with Spectrum!

# Http client wrapper
require 'faraday'

# Json library
require 'json'

# Pry!
require 'pry'

# Pretty print
require 'pp'
require 'paint'

def nn_banner(char = '+', color = :green, *va)
  print "[#{Paint[char, color, *va]}] "
end

module Spectrum
  # Program version
  SPECTRUM_VERSION = '0.1.0'.freeze

  # Node template
  PARSER_CODE = ENV['PARSER_CODE'] || './geekspec_parser.js'
  NODE_FD = '/proc/self/fd/0'.freeze
  NODE_INPUT = "fs.readFileSync('#{NODE_FD}')".freeze

  PARSER = <<EoCMD.freeze
node -e "console.log(JSON.stringify(require('#{PARSER_CODE}').parse(#{NODE_INPUT}.toString())))"
EoCMD

  #########################################################################
  #   Copyright 2019 duangsuse
  #
  #   Licensed under the Apache License, Version 2.0 (the "License");
  #   you may not use this file except in compliance with the License.
  #   You may obtain a copy of the License at
  #
  #       http://www.apache.org/licenses/LICENSE-2.0
  #
  #   Unless required by applicable law or agreed to in writing, software
  #   distributed under the License is distributed on an "AS IS" BASIS,
  #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  #   See the License for the specific language governing permissions and
  #   limitations under the License.
  #########################################################################

  # 代码质量非常垃圾，非常脚本化，所以就 Apache2 了，开玩笑，好吧，是我懒得 extract...
  # Parser 我没有重写，虽然重写正则也比较简单，因为首先 GeekSpec 是 CFG，其次它的确很简单... 几乎正则都够了
  # 这个是个小工具，我希望让它成为一个测试用的客户端，暂时还没有其他打算
  # 以后的 GeekApk Ruby 客户端可能有一部分代码可以使用这个生成，当然这次直接元编程就可以了，不需要生成代码字符串了

  class Interface
    class Argument
      def parse_extra(_json = @name)
        if @name.include?(':')
          if @name.include?('-')
            search = @name.match(/(\S+)-(\S+):(\S+)/)
            return { type: search[3], param_location: search[2], real_name: search[1] }
          end
          search = @name.match(/(\S+):(\S+)/)
          { type: search[2], real_name: search[1] }
        end
      end

      def to_map
        { name: @name, required: @required, options: @options }
      end

      def initialize(json)
        @name = json['name']
        @required = json['required']
        @options = json['options']

        begin
          @extra = parse_extra
        rescue StandardError
          @extra = nil
        end
      end

      def required_to_s
        @required ? '' : Paint['?', :red, :reveal]
      end

      def options_to_s
        return '' if @options.nil?

        list = @options.map { |x| Paint[x, :green, :bold] }.join(', ') unless @options.empty?
        "{#{list}}"
      end

      def name_to_s
        unless @extra.nil?
          name_desc = Paint[extra[:real_name], :bright, :blue]
          if extra[:type]
            type_desc = Paint[extra[:type], :green]
            if extra[:param_location]
              return "#{name_desc}-#{Paint[extra[:param_location], :magenta, :bold]}:#{type_desc}"
            end

            return "#{name_desc}:#{type_desc}"
          end
          return (Paint[extra[:real_name], :blue]).to_s
        end

        @name
      end

      def to_s
        "#{name_to_s}#{required_to_s}#{options_to_s}"
      end

      attr_accessor :name, :required, :options, :extra
    end

    class ReturnTypeAndObject
      def initialize(type, of)
        @type = type
        @name = of
      end

      def to_s
        "#{Paint[type, :blue]}:#{Paint[name, :green]}"
      end
      attr_accessor :name, :type
    end

    class ReturnTypesAndNames
      class ReturnTypeAndName
        def initialize(type, name)
          @type = type
          @name = name
        end

        def to_s
          "$#{Paint[name, :green]}:#{Paint[type, :blue]}"
        end
        attr_accessor :name, :type
      end

      def initialize(json)
        @return_ary = json.map { |sub| ReturnTypeAndName.new(sub['type'], sub['name']) }
      end

      def to_s
        "[#{return_ary.join(', ')}]"
      end
      attr_accessor :return_ary
    end

    def initialize(json)
      @method = json['method']
      @name = json['name']
      @args = json['args'].map { |a| Argument.new(a) }
      @return = Interface.map_return_type(json['return'])
      @return_original = json['return']
      @location = json['url']
    end

    def make_api_method(showcase)
      if $DEBUG
        nn_banner('-', :yellow)
        puts "Making API method #{self}"
      end

      spec = self # to be package (reference back to here)

      showcase.class.define_method(name) do |*params, &block|
        my_spec = spec # packaged spec base

        my_auth = auth
        my_conn = conn

        ClientShowcase.handler(my_spec, my_conn, my_auth, params, &block)
      end
    end

    def self.map_return_type(json)
      if json.is_a? Hash
        return ReturnTypeAndObject.new(json['type'], json['of'])
      elsif json.is_a? Array
        return ReturnTypesAndNames.new(json)
      elsif json.is_a? String
        return json
      elsif json.nil?
        return nil
      end

      warn "Cannot recognize json sub-structure #{json}"
    end

    def url_to_s
      # parser = URI::Parser.new
      # path = parser.parse(@location)
      convert = lambda do |cannon_part|
        if v = cannon_part.match(/\{(.*)\}/)
          return "{#{Paint[v[1], :blue, :bold]}}"
        else
          return Paint[cannon_part, :magenta]
        end
      end
      @location.split('/').reduce { |is, i| "#{is}/#{convert.call(i)}" }
    end

    def return_to_s
      return if @return.nil?

      " #{Paint['->', :bold, :yellow]} #{@return}"
    end

    def to_s
      "#{Paint[name, :bright, :yellow]}(#{args.join(', ')})#{return_to_s}\n  #{Paint['=', :red]} #{Paint[@method, :cyan, :bold]} #{url_to_s}"
    end

    def to_map
      {
        name: name, args: args.map(&:to_map), return: @return_original, method: method, url: @location
      }
    end

    attr_accessor :name, :args, :return, :method, :location, :return_original
  end

  ShowcaseObject = Object

  class GeekAuth
    def initialize(uid, tok, adm = '')
      user = uid
      token = tok
      server = adm
    end

    attr_accessor :user, :token, :server

    def to_s
      if server
        "Admin(#{user}):#{token}:#{server}"
      else
        "User(#{user}):#{token}"
      end
    end

    def to_cookie
      if server
        "gaUser=#{user}; gaHash=#{token}; gaModTok=#{server}"
      else
        "gaUser=#{user}; gaHash=#{token}"
      end
    end
  end

  class ClientShowcase < ShowcaseObject
    def initialize(interfaces)
      @conn = Faraday.new(url: 'http://127.0.0.1:8080')
      @apis = interfaces
      @auth = GeekAuth.new(-1, '')
    end

    def instance_api_methods
      apis.each { |api| api.make_api_method(self) }
    end

    def show_help
      nn_banner('i', :cyan)
      puts "Faraday connection: #{Paint['conn', :blue]}, Apis: #{Paint['apis', :blue]}, Auth configuration: #{Paint['auth', :blue]}"
      nn_banner
      puts "run #{Paint['show [:apiName|apiNameRegex|apiLocationRegex]', :green, :bold]} to see api documentation, run #{Paint['list', :green, :bold]} to view api list, have fun!"
    end

    alias api_help show_help

    def on_load
      show_help
      Pry.config.prompt = [proc do |obj, nest_level, _|
        pre = "#{obj} :: " unless obj == self
        "spectrum(#{pre}#{nest_level}.#{_.input_array.size})> "
      end, proc { '*' }]

      instance_api_methods
    end

    def show(item_id = nil)
      if item_id
        return puts apis.find { |a| a.name == item_id.to_s } if item_id.is_a? Symbol

        apis.find_all { |i| i.name.match(item_id) || i.location == item_id }.each { |r| puts r; puts }
      else
        apis.each { |a| puts a; puts }
      end
      nil
    end

    def list
      apis.each { |a| puts a.name }
      nil
    end

    def nil?
      false
    end

    def puts(*va)
      ::Kernel.method(:puts).call(*va)
    end

    def to_s
      'GeekApk'
    end

    attr_accessor :apis, :conn, :auth
  end

  def ClientShowcase.require_auth?(spec)
    # true # should be optimized in future releases
    # return true if %w[createUser resetSharedHash deleteUser flagUser createCategory renameCategory deleteCategory deleteApp transferAppCategory transferAppOwner].include?(spec.name)
    return true if spec.location.start_with?('/admin')
    return true if spec.location.start_with?('user') && (spec.method != 'GET')
    return false if spec.location.start_with?('/timeline')
    return true if spec.location.start_with?('/notification')
    return true if spec.location.start_with?('/app') && (spec.method != 'GET')
    return true if spec.location.start_with?('/appUpdate') && (spec.method != 'GET')
    return true if spec.location.start_with?('/comment', '/follow', '/star') && (spec.method != 'GET')

    false
  end

  def ClientShowcase.from_json(json_str)
    json = json_str.yield_self { |c| JSON.parse(c) }

    interfaces = json.map { |i| Interface.new(i) }

    new interfaces
  end

  def ClientShowcase.from_code(code)
    json_str = Spectrum.translate_extra_node(code.gsub(/#.*$/, ''))

    from_json json_str
  end

  def ClientShowcase.from_file(path = Dir.glob('*.geekspec').first)
    text = File.read(path)
    if path.end_with?('.json')
      return from_json(text)
    else
      return from_code(text)
    end
  end

  def ClientShowcase.map_response(spec, resp)
    if (r = spec.return).is_a? ReturnTypeAndObject
      json = JSON.parse(resp.body)
      mapper = "map_resp_#{r.name}"
      if ClientShowcase.respond_to?(mapper)
        case r.type
        when 'object' then return ClientShowcase.send(mapper, json)
        when 'array' then return json.map { |it| ClientShowcase.send(mapper, it) }
        end
      end
      return json
    elsif spec.return.is_a? ReturnTypesAndNames
      return JSON.parse(resp.body)
    end

    resp
  end

  def ClientShowcase.setup_auth(req, auth)
    puts "Setup Auth #{auth}(#{auth.to_cookie}) for request #{req}" if $DEBUG
    # gaUser, gaHash, gaModTok
    req.headers['Cookie'] = auth.to_cookie
  end

  # Should be optimized in future releases
  def ClientShowcase.handler(my_spec, my_conn, my_auth, params, &action)
    if $DEBUG
      nn_banner('^', :yellow)
      puts "Committing request #{my_spec.method} #{my_conn.url_prefix}#{my_spec.location}"
      nn_banner('#', :cyan)
      print "Using auth #{my_auth}, " if ClientShowcase.require_auth?(my_spec)
      puts "with parameters #{params}"
    end

    response = my_conn.send(my_spec.method.downcase) do |req|
      finally = my_spec.location.dup
      body = ''
      url_type_map = {}
      type_map = {}

      action.call(req) if action

      # must = my_spec.args.collect { |a| a.required }.take_while { |p| p }
      must = my_spec.args.take_while(&:required)
      puts must if $DEBUG

      if !my_spec.args.empty? && params.empty? || params.size > my_spec.args.size
        nn_banner('!', :red)
        warn "Arity mismatch: expected #{my_spec.args.size} (not optional #{must.size}), got #{params.size}"
        if must.empty?
          nn_banner('*', :blue)
          puts 'NOTE: fill first optional argument to get started'
        end
        return puts my_spec
      end

      if params.size < must.size
        nn_banner('-', :red)
        warn "Should not ignore parameter #{must[params.size..must.size].join(', ')}"
      end

      rest_must = my_spec.args[must.size..-1].zip(my_spec.args.each_index).find_all { |ai| ai[0].required }
      last_rest_index = rest_must.empty? ? -1 : rest_must.max { |ai| ai[1] }[1]
      puts "Rest non-optional arguments #{rest_must}" if $DEBUG

      if params.size <= last_rest_index
        nn_banner('!', :red)
        puts "Should fill rest non-optional parameters #{rest_must.join(', ')}, expected to index #{last_rest_index + 1}, actual: #{params.size}"
      end

      params.zip(my_spec.args).each do |p|
        if p[1].options && !p[1].options.empty?
          unless p[1].options.include?(p.first)
            nn_banner('-', :red)
            warn "Warning, parameter #{p.first} not in range #{p[1].options}"
          end
        end

        case (t = p[1].extra)[:param_location]
        when 'path'
          finally.gsub!("{#{t[:real_name]}}", p.first.to_s)
          url_type_map[t[:real_name]] = t[:type]
        when 'body' then body.empty? ? (body = p.first) : (warn "Duplicate body variable processing #{my_spec}")
        when nil
          type_map[t[:real_name]] = t[:type]
          req.params[t[:real_name]] = p.first
        end
      end

      req.body = body unless body.empty?

      nn_banner if $DEBUG
      puts "Finally url #{finally} (#{req.params}), UT map #{url_type_map}, map #{type_map}, body = #{body}" if $DEBUG

      req.url(finally)

      ClientShowcase.setup_auth(req, my_auth) if ClientShowcase.require_auth?(my_spec)
    end

    ClientShowcase.map_response(my_spec, reponse)
  end

  def self.make_json(apis)
    f = File.new("spectrum_#{ARGV[0].gsub(/\..*$/, '')}.json", 'w+')

    begin
      f.write(JSON.pretty_generate(apis.map(&:to_map)))
      f.flush
      f.close
      puts "Wrote to #{f.path}"
    rescue Exception => ex
      warn "Failed to write to #{f.path}: #{ex}"
      File.delete(f)
    end
  end

  def self.right_away(spec)
    pp spec, indent_size: 2 if $DEBUG
    interfaces = spec.map { |i| Interface.new(i) }
    me = ClientShowcase.new(interfaces)

    case ARGV[1]
    when 'show' then return me.show
    when 'licence' then return puts 'Apache 2.0'
    when 'help' then return puts '👆 Help contents above'
    when 'json' then return make_json(interfaces)
    when 'debug' then $DEBUG = true
    end

    me.on_load
    Object.method(:pry).call(me)
  end

  # synchronous, bit buggy, not robust
  def self.translate_extra_node(code)
    #  %x[#{"cat<<CODE\n" + code + "\nCODE\n|" + PARSER}]
    #  %x[#{"echo " + code + '|' + PARSER}]
    f = File.new("Code_#{rand(0..100)}", 'w+')

    begin
      f.write(code)
      f.flush

      result = `#{PARSER.gsub(NODE_FD, f.path)}`
    ensure
      File.delete(f)
    end

    result
  end

  # CLI launcher
  def self.start(_args = ARGV)
    puts "Spectrum v#{SPECTRUM_VERSION}: usage: #{$PROGRAM_NAME} <spec json file> [command]{show,licence,help,json,debug}"

    return unless (ARGV.size <= 2) && (ARGV.size >= 1)

    code = File.read(ARGV.first)

    unless ARGV.first.end_with?('.json') || ENV['JSON']
      translated = translate_extra_node(code.gsub(/#.*$/, ''))

      json = translated.yield_self { |c| JSON.parse(c) }

      return right_away(json)
    end

    right_away(JSON.parse(code))
  end
end

# invokes main function if this script is running not as a library
Spectrum.start if $PROGRAM_NAME == __FILE__
