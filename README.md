### In another directory

1. Clone the Closure Compiler code: `git clone git@github.com:google/closure-compiler.git`
2. `cd closure-compiler`
3. Build the Closure Compiler locally: `mvn -DskipTests`, this should install an artifact into your `~/.m2` which you can depend on by adding the coordinates below to your `build.boot`. (In this repo it has already been added.)

        [com.google.javascript/closure-compiler-unshaded "1.0-SNAPSHOT"]

### In this directory

1. Install NPM packages: `yarn install`
2. Build foreign libs vector: `boot repl -e "(do (compute-foreign-libs) (System/exit 0))"`
3. Start the build: `boot dev`
