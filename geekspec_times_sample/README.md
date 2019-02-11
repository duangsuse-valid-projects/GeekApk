# Times

Simple commentable blogging web application

## Installation

Add this line to your application's Gemfile:

```ruby
gem 'times'
```

And then execute:

   `$ bundle`

Or install it yourself as:

   `$ gem install times`

## Usage

start server with `rake server`

## Development

After checking out the repo, run `bin/setup` to install dependencies. Then, run `rake spec` to run the tests. You can also run `bin/console` for an interactive prompt that will allow you to experiment.

To install this gem onto your local machine, run `bundle exec rake install`. To release a new version, update the version number in `version.rb`, and then run `bundle exec rake release`, which will create a git tag for the version, push git commits and tags, and push the `.gem` file to [rubygems.org](https://rubygems.org).

### Models

__Post:__ _String_ author, _Time_ time, _String_ title, _String_ summary, _String_ text, _Integer_ views, _Set_ comments

__Comment:__ _String_ author, _Time_ time, _String_ text, _Integer_ likes

### APIs

```http
GET /api/version -> version

GET /api -> num_posts
GET /api/<nth> -> Post author, title, summary, date
GET /api/<nth>/detail -> Post
GET /api/<nth>/views -> num_views
GET /api/<nth>/comments -> [Comment
POST /api/<nth>/comment/<author> body=<text> -> new_len
POST /api/<nth>/comment/<time>/like
GET /api/<nth>/comments/len -> num_comments

DELETE /api/pop/<pass> -> new_len
POST /api/<author>/<pass>/<title> body=<text> (auth) -> new_len

GET /api/realtime
```

### Signals

__SIGQUIT:__ output data and exit

__SIGUSR1:__ output data

__SIGUSR2:__ clear anti-spam log

## Contributing

Bug reports and pull requests are welcome on GitLab at [here](https://gitlab.com/geekage/Times). This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

The gem is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Code of Conduct

Everyone interacting in the Times project’s codebases, issue trackers, chat rooms and mailing lists is expected to follow the [code of conduct](https://gitlab.com/geekage/times/blob/master/CODE_OF_CONDUCT.md).
