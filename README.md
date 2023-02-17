# TinyUrl clone

I'm working on this as a personal project, and we'll see how it goes from there.

The idea is to make a self-hosted version of tiny url.

### About the project

Project uses Ktor and Kotlin, it has a small api for creating urls.

### Roadmap (incomplete)

- [x] Create the most basic version covered in tests without any users
- [ ] Tests
- [ ] Add authentication and authorization
- [ ] Add filesystem as a storage - most basic version without database. This way there's no db maintenance
- [ ] Add database as a storage
    - [ ] Docker and testcontainers
- [ ] Add statistics for how many times a route has been called
- [ ] Add expiry of url
- [ ] Add feature that doesn't return 301 but instead 200 and where that link is leading to
- [ ] Add ability to limit creation of account to only one user
- [ ] Add ability to limit creation of links to only logged-in user, or to be public and allow anyone on the internet
  to create a tiny url
- [ ] Add a job to check links periodically to see if there are any broken links, and then check with user what to do
  about those links - delete them, replace them or whatever.