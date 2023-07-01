function install_plugin() {
  curl -L -o "$1/plugins/MCDash.jar" "https://api.spiget.org/v2/resources/110687/download"
  echo "Plugin installed. Restart your server to complete the installation."
}

if [[ -n $1 ]]; then
  install_plugin "$1"
  echo "MCDash | [IDONE]"
  exit
fi

echo "Available Minecraft servers:"
IFS=$'\n'

servers=($(find "/" -name bukkit.yml -exec dirname {} \; 2>/dev/null))

select server in "${servers[@]}"; do
  if [[ -n $server ]]; then
      install_plugin "$server"
      break
  else
      echo "Invalid selection. Please try again."
  fi
done
