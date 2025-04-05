import os
import subprocess
import sys


def load_env_file(path=".env"):
    if not os.path.exists(path):
        print(f"⚠️  {path} file not found. Skipping .env load.")
        return
    with open(path) as file:
        for line in file:
            if "=" in line and not line.lstrip().startswith("#"):
                key, value = map(str.strip, line.strip().split("=", 1))
                os.environ[key] = value


def run(command, step_name):
    print(f"\n🟡 {step_name}...")
    try:
        subprocess.run(command, check=True, env=os.environ)
        print(f"✅ {step_name} — done")
    except subprocess.CalledProcessError:
        print(f"❌ {step_name} — failed")
        sys.exit(1)


def main():
    load_env_file()
    steps = [
        #         (["./gradlew", "clean"], "Clean"),
        #         (["./gradlew", "flywayClean", "-Dflyway.cleanDisabled=false"], "Clean"),
        (["./gradlew", "spotlessApply"], "Spotless apply"),
        (["./gradlew", "assemble"], "Gradle build"),
        (["./gradlew", "flywayMigrate"], "Flyway migrate"),
        (["./gradlew", "generateJooq"], "jOOQ code generation"),
        (["./gradlew", "test"], "Running tests"),
    ]
    for command, step_name in steps:
        run(command, step_name)


if __name__ == "__main__":
    main()
