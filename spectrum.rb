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

# Program version
VERSION = "0.1.0"

# Node template
PARSER_CODE = ENV['PARSER_CODE'] || './geekspec_parser.js'
NODE_FD = '/proc/self/fd/0'
NODE_INPUT = "fs.readFileSync('#{NODE_FD}')"

PARSER = <<EoCMD
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

class Interface
  class Argument
    def parse_extra(json = @name)
      if @name.include?(':')
        if @name.include?('-')
          search = @name.match(/(\S+)-(\S+):(\S+)/)
          return { type: search[3], param_location: search[2], real_name: search[1] }
        end
         search = @name.match(/(\S+):(\S+)/)
         return { type: search[2], real_name: search[1] }
      end
    end

    def initialize(json)
      @name = json['name']
      @required = json['required']
      @options = json['options']

      begin
        @extra = parse_extra
      rescue
        @extra = nil
      end
    end

    def required_to_s
      if @required then '' else Paint['?', :red, :reveal] end
    end

    def options_to_s
      return '' if @options == nil
      list = @options.map { |x| Paint[x, :green, :bold] }.join(', ') unless @options.empty?
      "{#{list}}"
    end

    def name_to_s
      unless @extra == nil
        name_desc = Paint[extra[:real_name], :bright, :blue]
        if extra[:type]
          type_desc = Paint[extra[:type], :green]
          if extra[:param_location]
            return "#{name_desc}-#{Paint[extra[:param_location], :magenta, :bold]}:#{type_desc}"
          end
          return "#{name_desc}:#{type_desc}"
        end
        return "#{Paint[extra[:real_name], :blue]}"
      end

      return @name
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

    def to_s; "[#{return_ary.join(', ')}]"; end
    attr_accessor :return_ary
  end

  def initialize(json)
    @method = json['method']
    @name = json['name']
    @args = json['args'].map { |a| Argument.new(a) }
    @return = Interface.map_return_type(json['return'])
    @location = json['url']
  end

  def self.map_return_type(json)
    if json.is_a? Hash
      return ReturnTypeAndObject.new(json['type'], json['of'])
    elsif json.is_a? Array
      return ReturnTypesAndNames.new(json)
    elsif json.is_a? String
      return json
    elsif json == nil
      return nil
    end
    warn "Cannot recognize json sub-structure #{json}"
  end

  def url_to_s
    #parser = URI::Parser.new
    #path = parser.parse(@location)
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
    return if @return == nil
    " #{Paint['->', :bold, :yellow]} #{@return}"
  end

  def to_s
    "#{Paint[name, :bright, :yellow]}(#{args.join(', ')})#{return_to_s}\n  #{Paint['=', :red]} #{Paint[@method, :cyan, :bold]} #{url_to_s}"
  end

  attr_accessor :name, :args, :return, :method, :location
end

ShowcaseObject = Object

class ClientShowcase < ShowcaseObject
  def initialize(interfaces)
    @apis = interfaces
  end

  def show
    apis.each { |a| puts a; puts }
    nil
  end

  def nil?; false; end

  def puts(*va); ::Kernel.method(:puts).call(*va); end

  def to_s; "GeekApk"; end

  attr_accessor :apis
end

def right_away(spec)
  pp spec, indent_size: 2 if $DEBUG
  interfaces = spec.map { |i| Interface.new(i) }
  me = ClientShowcase.new(interfaces)

  case ARGV[1]
    when 'show' then return me.show
  end

  Object.method(:pry).call(me)
end

# synchronous, bit buggy, not robust
def translate_extra_node(code)
#  %x[#{"cat<<CODE\n" + code + "\nCODE\n|" + PARSER}]
#  %x[#{"echo " + code + '|' + PARSER}]
  f = File.new("Code_#{rand(0..100)}", 'w+')

  begin
    f.write(code)
    f.flush

    result = %x(#{PARSER.gsub(NODE_FD, f.path)})
  ensure
    File.delete(f)
  end

  return result
end

# CLI launcher
def start(args = ARGV)
  puts "Spectrum v#{VERSION}: usage: #{$0} <spec json file> [command]"

  return unless ARGV.size <= 2

  code = File.read(ARGV.first)

  unless ARGV.first.end_with?('.json') or ENV['JSON']
    translated = translate_extra_node(code.gsub(/#.*$/, ''))

    json = translated.yield_self { |c| JSON.parse(c) }

    return right_away(json)
  end

  right_away(JSON.parse(code))
end

# invokes main function if this script is running not as a library
start if $0 == __FILE__
