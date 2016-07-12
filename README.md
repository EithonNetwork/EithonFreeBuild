# EithonFreeBuild

A FreeBuild plugin for Minecraft; Like gamemode survival, but you are not attacked by mobs (and can't attack them) and you don't get damaged

## Prerequisits

* None

## Functionality

The plugin provides administrative commands for going in and out of FreeBuild mode.

### Administrative commands

* on: Go into FreeBuild mode
* off: Leave FreeBuild mode

### What is FreeBuild mode?

When the user changes to FreeBuild mode, the following applies:

* The user can't be attacked by monsters
* The user can't attack monsters
* The user can't be damaged

## Release history

### 2.0 (2016-06-30)

* CHANGE: Now uses EithonCommand

### 1.4 (2016-06-30)

* CHANGE: Minecraft 1.10

### 1.3 (2015-10-18)

* CHANGE: Refactoring EithonLibrary.

### 1.2 (2015-09-10)

* NEW: Added a /freebuild release <player> command to release players from cool down.

### 1.1 (2015-08-10)

* NEW: Now restores information about freebuild players after a restart.
* CHANGE: All time span configuration values are now in the general TimeSpan format instead of hard coded to seconds or minutes or hours.

### 1.0.4 (2015-07-24)

* BUG: Freebuilder could damage monsters by shooting arrows.

### 1.0.3 (2015-07-21)

* CHANGE: Now sends an optional command when the user tries to fly in survival mode.

### 1.0.2 (2015-07-14)

* BUG: Minor bug: "Survival mode is now active (freebuild is OFF)" appears two times

### 1.0.1 (2015-07-08)

* BUG: Now shows subcommands if no subcommand was given.

### 1.0 (2015-04-18)

* New: First Eithon release.
