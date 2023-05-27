# TinyUrl clone

I'm working on this as a personal project, and we'll see how it goes from there.

The idea is to make a self-hosted version of tiny url.

### About the project

Project uses Kotlin, Ktor, SQLite, and it has a small api for creating urls, users and has statistics for logged-in
users.

### Intentions of use

* For personal use once deployed.
* For API and e2e testing. I've worked for IT Bootcamp, and we worked with Selenium and Postman, this project will help
  students test some familiar website without ads.
* For anyone else who wants to use OSS.

### Roadmap (incomplete)

For v1.0.0:
- [x] Create the most basic version covered in tests without any users
- [x] Add feature that doesn't return 301 but instead 200 and where that link is leading to
- [x] Add expiry of url
- [x] Add Dokka for code documentation
- [x] Add dependency injection (Koin)
- [ ] Tests
  - [ ] Maintain test coverage above 90%
  - [x] Added Kover as code coverage tool
- [ ] Add authentication and authorization
  - [x] Add JWT
  - [ ] Add security tests
- [x] Add SQLite - most basic version with local. This way there's no db maintenance.
  - [x] in memory SQLite for tests
  - [x] add user
  - [x] add liquibase
  - [ ] add JPA
  - [ ] see if queries are blocking and wrap them in runBlocking
- [x] Add statistics for how many times a route has been called
  - [ ] Add source query parameter so statistics can be more accurate from where request originated. See if there's any
    alternative for this
- [x] Add ability to limit creation of account to only one user
- [x] Add ability to limit creation of links to only logged-in user, or to be public and allow anyone on the internet
  to create a tiny url
- [ ] Add frontend (maybe with Kotlin/React)
- [x] Add openApi/swagger
- [ ] Add GraalVM for smaller footprint (cpu, memory..)
- [ ] Add auditing
- [ ] Add metrics

Add later:

- [ ] Add Oauth2 for 3rd party services if possible so user can register via google, git etc...
- [ ] Add Postgres (as another implementation)
  - [ ] Docker and testcontainers (docker should be added anyway so people can deploy in their local env in minutes and
    test stuff out)
- [ ] Add a job to check links periodically to see if there are any broken links, and then check with user what to do
  about those links - delete them, replace them or whatever.

---

### Gradle tasks

`./gradlew dokkaHtml` to generate html documentation for the code. This task doesn't print out location of created,
but you can look it up manually. It should be found in `build/dokka/index.html`.

`./gradlew koverHtmlReport` generates html code coverage. This tasks will give you link to html.

`./gradlew run` - part of application plugin.

`./gradlew buildFatJar` - Builds a combined JAR of project and runtime dependencies.

### Running with java

`java -jar build/libs/tinyurl-fat.jar` starts a fat jar. It will also create tinyUrl.sqlite file.

`java -jar build/libs/tinyurl-fat.jar -config=pathToCustomConfig.yaml` starts a fat jar with custom configuration. This
option is used for deployments where you can add SSL, change JWT properties etc. Note that `=` must be used for
arguments because Ktor treats them as separate arguments if there is just a space instead of equals.

## Released on May 27. 2023

I've successfully released the server with swagger, the URL is [https://tajni.link/swagger]().
I've searched for names that could work for this and didn't find any of them available except tajni.link. It's hosted
for $5 a year + $0.5 monthly for hosted zone and $3.5 monthly for AWS Lightsail instance.

There are some stability issues which I'll address in next couple of days to make everything to work.

### What does tajni mean?

"tajni" is how "tiny" it's read in Serbian (my home country), but it doesn't have the same meaning. In Serbian, it means
"secret", but that was just a coincidence. So [https://tajni.link]() is like secret.link translated.  
