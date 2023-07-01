#!/bin/bash

find_minecraft_servers() {
  local root_dir=$1

  while IFS= read -r -d '' file; do
    local server_path=$(dirname "$file")
    local plugins_dir="$server_path/plugins"

    if [[ -d "$plugins_dir" && ! -d "$plugins_dir/MinecraftDashboard" ]]; then
      echo "MCDash | [SERVER]$server_path[SERVER]"
    fi
  done < <(find "$root_dir" -name "bukkit.yml" -type f -print0)
}

find_minecraft_servers "/"

echo "MCDash | [DONE]"