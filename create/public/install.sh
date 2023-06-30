#!/bin/bash
export DEBIAN_FRONTEND=noninteractive

ROOT="/opt/minecraft"

SOFTWARE=$1
USER=$2
ID=$3
MC_PORT=$4
PANEL_PORT=$5
VERSION=$6
NAME=$7

function quit() {
    echo Error: $1
    exit 1
}

function download() {
  if wget -S --spider "$1" 2>&1 | grep -q 'HTTP/1.1 200 OK'; then
    wget -qO "$2" "$1"
  fi
}

if [ -z "${SOFTWARE}" ]; then
    quit "Software not specified"
fi

if [ -z "${USER}" ]; then
    quit "User not specified"
fi

if [ -z "${ID}" ]; then
    quit "ID not specified"
fi

if [ -z "${MC_PORT}" ]; then
    quit "Minecraft port not specified"
fi

if [ -z "${PANEL_PORT}" ]; then
    quit "Panel port not specified"
fi

if [ -z "${VERSION}" ]; then
    quit "Version not specified"
fi

if [ -z "${NAME}" ]; then
    quit "Name not specified"
fi

# Check if ports are used (lsof)
if lsof -Pi :${MC_PORT} -sTCP:LISTEN -t >/dev/null ; then
    quit "Minecraft port already in use"
fi

if lsof -Pi :${PANEL_PORT} -sTCP:LISTEN -t >/dev/null ; then
    quit "Panel port already in use"
fi

INSTALLATION_PATH="${ROOT}/${ID}"

if [ -d "${INSTALLATION_PATH}" ]; then
    quit "Installation already exists"
fi

if [ ! -d "${ROOT}" ]; then
    mkdir -p "${ROOT}"
fi

if [ ! -f "/usr/bin/wget" ]; then
    apt update > /dev/null 2>&1
    apt install -y wget > /dev/null 2>&1
fi

if [ ! -f "/usr/bin/jq" ]; then
    apt update > /dev/null 2>&1
    apt install -y jq > /dev/null 2>&1
fi

mkdir -p "${INSTALLATION_PATH}" || quit "Unable to create root directory"

cd "${INSTALLATION_PATH}" || quit "Unable to change directory"

if [ "${SOFTWARE}" == "paper" ]; then
  BUILD=$(curl -s "https://papermc.io/api/v2/projects/paper/versions/${VERSION}" | jq -r '.builds[-1]')
  download "https://papermc.io/api/v2/projects/paper/versions/${VERSION}/builds/${BUILD}/downloads/paper-${VERSION}-${BUILD}.jar" "server.jar"
elif [ "${SOFTWARE}" == "spigot" ]; then
  download "https://download.getbukkit.org/spigot/spigot-${VERSION}.jar" "server.jar"
    if [ ! -f "server.jar" ]; then
      download "https://download.getbukkit.org/spigot/spigot-${VERSION}-R0.1-SNAPSHOT.jar" "server.jar"
    fi
elif [ "${SOFTWARE}" == "purpur" ]; then
  download "https://api.purpurmc.org/v2/purpur/${VERSION}/latest/download" "server.jar"
else
    quit "Invalid software"
fi

if [ ! -f "server.jar" ]; then
    quit "Unable to download server jar"
fi

echo "eula=true" > eula.txt

cat > server.properties <<EOF
server-port=${MC_PORT}
EOF

if [ ! -d "${ROOT}/java/" ]; then
  mkdir -p "${ROOT}/java/"
  cd "${ROOT}/java/" || quit "Unable to change directory"

  download "https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz" "java.tar.gz"

  if [ ! -f "java.tar.gz" ]; then
    quit "Unable to download java"
  fi

  tar -xzf java.tar.gz
  rm java.tar.gz

  mv jdk-17*/* .
fi

mkdir -p "${INSTALLATION_PATH}/plugins" || quit "Unable to create plugins directory"
cd "${INSTALLATION_PATH}/plugins" || quit "Unable to change directory"

download "https://api.spiget.org/v2/resources/110687/download" "MCDash.jar"

if [ ! -f "MCDash.jar" ]; then
    quit "Unable to download MCDash plugin"
fi

mkdir -p "${INSTALLATION_PATH}/plugins/MinecraftDashboard" || quit "Unable to create MCDash directory"

cat > "${INSTALLATION_PATH}/plugins/MinecraftDashboard/config.yml" <<EOF
port: ${PANEL_PORT}
EOF

cat > "${INSTALLATION_PATH}/plugins/MinecraftDashboard/accounts.yml" <<EOF
accounts:
  $USER
EOF

useradd minecraft-${ID} -d "${INSTALLATION_PATH}" -s /bin/bash || quit "Unable to create user"
chown -R minecraft-${ID}:minecraft-${ID} "${INSTALLATION_PATH}" || quit "Unable to change owner"

cat > "/etc/systemd/system/minecraft-${ID}.service" <<EOF
[Unit]
Description=Minecraft Server - ${NAME}
After=network.target

[Service]
WorkingDirectory=${INSTALLATION_PATH}
User=minecraft-${ID}
Restart=on-failure
RestartSec=5
ExecStart=${ROOT}/java/bin/java -jar server.jar nogui
[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable "minecraft-${ID}"
systemctl start "minecraft-${ID}"

# Wait until bukkit.yml is created, break after 100 tries

for i in {1..60}; do
  if [ -f "${INSTALLATION_PATH}/bukkit.yml" ]; then
    break
  fi
  sleep 1
done

if [ ! -f "${INSTALLATION_PATH}/bukkit.yml" ]; then
  quit "Could not start server"
fi

echo "Server started"