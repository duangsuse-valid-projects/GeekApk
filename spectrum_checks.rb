require './spectrum'
require 'rspec'

require 'socket'
include Socket::Constants

RSpec.configure do |config|
  config.disable_monkey_patching!

  config.expect_with :rspec do |c|
    c.syntax = :expect
  end
end

SERVER_PORT = 8080


#sock_addr = Socket.pack_sockaddr_in(SERVER_PORT, 'localhost')

begin
  socket = TCPSocket.new('localhost', SERVER_PORT)
  #socket.bind
rescue Errno::EADDRINUSE
  puts "Server already started at port #{SERVER_PORT}"
rescue
  puts "Starting test server"
  #$server = open('./gradlew bootRun')
  out, write = IO.pipe
  $server = Process.spawn('./gradlew bootRun', :out => write)
  puts "Created sub-process #{$server}"

  # Wait until server is running
  while (line = out.gets)
    puts line
    if line.start_with?('GeekApk Server is now running')
      break
    end
  end
end

at_exit do
  puts "Terminating sub-process #{$server}"

  Process.kill(:TERM, $server) rescue puts "Failed to kill process"
end

RSpec.describe 'Hello' do
  it 'run this test' do
  end
end

RSpec.describe 'GeekApk' do
  ga = Spectrum::ClientShowcase.from_file('spectrum_geekapk_v1b_api.json')
  ga.instance_api_methods

  it 'accepting requests' do
    puts (v = ga.serverVersion)
    expect(v).to eq('0.1.0')
  end
end
