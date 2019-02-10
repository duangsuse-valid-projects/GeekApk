#!env -S ruby -W2 -w -v -E UTF-8 -T0

# Spectrum - a ruby HTTP client program paired with GeekSpec DSL

# GeekApk - https://github.com/duangsuse/GeekApk
#
# Write your API specifications with GeekSpec markup and load them with Spectrum!

# Http client wrapper
require 'faraday'

# Json library
require 'json'

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

def right_away(spec)
  puts spec
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
  puts "Spectrum v#{VERSION}: usage: #{$0} <spec json file>"

  return unless ARGV.size == 1

  code = File.read(ARGV.first)

  unless ARGV.first.end_with?('.json') or ENV['JSON']
    translated = translate_extra_node(code.gsub(/#.*$/, ''))
    require 'pry'
    translated.pry
    json = translated.yield_self { |c| JSON.parse(c) }

    return right_away(json)
  end

  right_away(JSON.parse(code))
end

# invokes main function if this script is running not as a library
start if $0 == __FILE__
