import subprocess
import sys
import os

def load_env_file(path=".env"):
    if not os.path.exists(path):
        print(f"⚠️  {path} file not found. Skipping .env load.")
        return

    with open(path) as file:
        for line in file:
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            if line.startswith("export "):
                line = line[len("export "):]
            key, value = line.split("=", 1)
            os.environ[key.strip()] = value.strip()


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
        (["./gradlew", "spotlessApply"], "Spotless apply"),
        (["./gradlew", "build"], "Gradle build"),
        (["./gradlew", "flywayMigrate"], "Flyway migrate"),
        (["./gradlew", "generateJooq"], "jOOQ code generation"),
    ]
    for command, step_name in steps:
        run(command, step_name)

if __name__ == "__main__":
    main()
