# Shared ci-scripts for multiple projects

Shared CI build and release scripts for several talsma-ict projects.  
This module is intended to be used as a git `submodule`.

## CI build process

### Git branches

Our build and release scripts are designed to be compatible with the 
`git-flow` branching model
[as described by Vincent Driessen in 2010](https://nvie.com/posts/a-successful-git-branching-model/).

This means SNAPSHOT builds can be created from the `develop` branch while releases
should be for [semver 2.0](https://semver.org/) compatible versions, tagged as such on the `master` branch.  

Our build and release scripts allow developers to focus mainly on the `develop` and other `feature` branches
and should only rarely -if ever- need to meddle with the `master` branch.

To perform a release, a developer just needs to push the corresponding code from `develop` to a
new `release/x.y.z` branch on `origin`.
When picked up by the CI system, results in a new version `x.y.z` being released, tagged and deployed on `master`.
Furthermore, the `develop` branch will be set to the _next snapshot_ version (e.g. `x.y.(z+1)-SNAPSHOT`).
If all succeeds, the `release/x.y.z` branch will be automatically deleted as well.

## Git submodules

### Understanding git submodules

First, read the  [official git documentation on submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules) 
if you haven't done so already.  
Another good read is the [working with submodules](https://blog.github.com/2016-02-01-working-with-submodules/) 
github blog post.

### Replacing existing directory by a submodule

_(Only if you have an existing directory to replace)_
First remove the existing directory from your repository

```bash
git rm -r .travis
git commit -m "Remove .travis directory (preparing for submodule)"
```

### Adding the submodule to a git project

If you want to add the _ci-scripts_ to your project into the `.travis` directory (for building with travis)
you can add them to your git repository:

```bash
git submodule add https://github.com/talsma-ict/ci-scripts.git .travis
git submodule update --init --recursive
git commit -am "Add ci-scripts repository as .travis submodule"
```

### Cloning a project with submodules

Just use the `--recursive` option with your `git clone` command:

```bash
git clone --recursive <project url>
```

## License

Just like our main repositories, the CI scripts are published under the [Apache 2.0 license](../LICENSE)
