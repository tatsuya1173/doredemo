# rails-app/Dockerfile
FROM ruby:3.2

RUN apt-get update -qq && apt-get install -y nodejs yarn sqlite3 libsqlite3-dev

WORKDIR /app

COPY . .

RUN gem install rails -v 7.1.3

# Blocked hosts対策（rails:3000 を許可）
RUN echo "Rails.application.configure do\n  config.hosts << 'rails'\nend" >> config/environments/development.rb || true

RUN bundle install || echo "bundle install skipped"

CMD ["rails", "server", "-b", "0.0.0.0", "-p", "3000"]
