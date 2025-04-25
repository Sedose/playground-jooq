import os
import subprocess
import sys

properties = {
    propertyKey: propertyValue
    for propertyKey, propertyValue in (
        line.strip().split("=", 1)
        for line in open("src/test/resources/application.properties")
    )
    if propertyKey and not propertyKey.startswith("#")
}

os.environ.update(
    DB_URL=properties["jdbc.url"],
    DB_USER=properties["jdbc.username"],
    DB_PASSWORD=properties["jdbc.password"],
)

taskArgumentsList = [
    # ["clean"],
    # ["flywayClean", "-Dflyway.cleanDisabled=false"],
    ["spotlessApply"],
    ["assemble"],
    ["flywayMigrate"],
    ["generateJooq"],
    ["test"],
]

for taskArguments in taskArgumentsList:
    fullCommand = ["./gradlew", *taskArguments]
    print(f"🟡 Running {fullCommand}…")
    executionResult = subprocess.run(fullCommand, env=os.environ)
    if executionResult.returncode != 0:
        print(f"❌ {fullCommand} — failed (exit {executionResult.returncode})")
        sys.exit(executionResult.returncode)
    print(f"✅ {fullCommand} — done")

print("🏆 All steps completed successfully")
