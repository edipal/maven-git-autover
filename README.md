# Introduction
There are cases when it is advantageous that the version is calculated instead of specified in the pom.
This is a *Maven* plugin that can automatically calculate a version based on *Git* tags. 
It should be registered as a Maven extension *.mvn/extensions.xml*  file.

# Git tag types
In git there are two types of tags: *lightweight* and *annotated*.
Annotated tags are usually used to mark releases (they have a comment/description) and lightweight tags in other cases.

# Configuration
How the plugin calculates the version is configurable.
The version will be calculated only for artifacts that have the version 0.0.0-SNAPSHOT.
A configuration has the following parameters:
- *versionTagRegex*: This is used to determine what tags should be considered version tags
- *includeGroupIds*: Only artifacts in this group ids will be processed. When no such group is specified artifacts from all groups ids will be processed
- *autoverBranchConfigs*: The branch configurations
   - *nameRegex*: This is used to determine the branches that should use this configuration
   - *stopOn*: the stop on setting to be used by branches that match the above pattern possible values
      - *ON_FIRST*: It will use the first annotation that it will find (regardles of the type)
      - *ON_FIRST_ANN*: It will use the first annotated branch that it will find
      - *ON_FIRST_LIGHT*: It will use the first light branch that it will find
The branch name is checked against the configured branches in the order from the configuration file and it stops on the first that matches.

# How the version is calculated
The version is calculated based on the configuration.
The plugin will look to the git history and "walk back" from current commit until it finds the first tag of the type configured for the branch and if the tag found is:
- *an annotated tag*: it will calculate the next version as the next minor version and add SNAPSHOT (For ex: when it finds tag 2.0.1, it will calculate the version as 2.0.2-SNAPSHOT)
- *a lightweight tag*: the version will be equal to the lightweight tag (For ex: when it finds tag 2.1.0-SNAPSHOT, it will calculate the version as 2.1.0-SNAPSHOT)

# Default configuration
The plugin has the following default configuration.
- *versionTagRegex* - [0-9]+\\.[0-9]+\\.([0-9]+)(-SNAPSHOT)?
- *includeGroupIds* - empty (all groups will be considered)
- *autoverBranchConfigs*
  - *master*                        - ON_FIRST_LIGHT
  - *release/.**                    - ON_FIRST_ANN
  - *feature/([A-Z0-9]+-[0-9]+)-.** - ON_FIRST_LIGHT
  - *bugfix/([A-Z0-9]+-[0-9]+)-.**  - ON_FIRST_ANN
  - *PR-\\d+*                       - ON_FIRST_ANN (for jenkins)
  - *.**                            - ON_FIRST
    
# Use non default configuration
If the default configuration is not OK for a certain maven module, a different configuration can be specified.
This custom configuration file should have the name *git.autover.conf.xml* and be placed in the *.mvn* folder of the module.

# How to disable it
The plugin execution can be disabled using *-Dautover.disable=true*. This way the version specified in the pom file will be used.
Also one could disable only the pom changes using *-Dautover.disable.pom.change=true*.
