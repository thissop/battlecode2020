# Battlecode 2020 

## Notes

### Misc. 

* I can test my bot against other bots from 2020 by just copying their code from GitHub and testing it against mine haha
* So apparently the bit shift operation ```1 << n``` equals ```2^n``` ... this leads me to think that if bytecode costs ever become a concern for us this year, Miles could investigate and implement some shortcuts like this above bit shift one (I remember him talking about similiar kinds  of things on a couple occasions). 

## From original README: 

This is the Battlecode 2020 scaffold, containing an `examplefuncsplayer`. Read https://2020.battlecode.org/getting-started!

### Project Structure

- `README.md`
    This file.
- `build.gradle`
    The Gradle build file used to build and run players.
- `src/`
    Player source code.
- `test/`
    Player test code.
- `client/`
    Contains the client.
- `build/`
    Contains compiled player code and other artifacts of the build process. Can be safely ignored.
- `matches/`
    The output folder for match files.
- `maps/`
    The default folder for custom maps.
- `gradlew`, `gradlew.bat`
    The Unix (OS X/Linux) and Windows versions, respectively, of the Gradle wrapper. These are nifty scripts that you can execute in a terminal to run the Gradle build tasks of this project. If you aren't planning to do command line development, these can be safely ignored.
- `gradle/`
    Contains files used by the Gradle wrapper scripts. Can be safely ignored.

