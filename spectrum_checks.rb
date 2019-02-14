require './spectrum'
require 'rspec'

RSpec.configure do |config|
  config.disable_monkey_patching!

  config.expect_with :rspec do |c|
    c.syntax = :expect
  end
end

RSpec.describe 'Hello' do
  it 'run this test' do
  end
end
