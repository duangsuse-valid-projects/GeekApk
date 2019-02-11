require 'bundler/setup'
require 'rack/test'
require 'rspec'
require 'times'

ENV['RACK_ENV'] = 'test'
require File.expand_path 'lib/times.rb'

module RSpecMixin
  include Rack::Test::Methods
  def app() TimesApp end
end

RSpec.configure do |config|
  config.include RSpecMixin
  # Enable flags like --only-failures and --next-failure
  config.example_status_persistence_file_path = '.rspec_status'

  # Disable RSpec exposing methods globally on `Module` and `main`
  config.disable_monkey_patching!

  config.expect_with :rspec do |c|
    c.syntax = :expect
  end
end
