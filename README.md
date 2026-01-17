<h1 align="center"><img src=".github/logo.png" alt="" width="32" height="32" style="vertical-align: middle;"> VoxelDash</h1>
<p align="center">A modern, beautiful web dashboard for managing your Minecraft server</p>
<p align="center">
  <a href="https://github.com/gnmyt/VoxelDash/stargazers"><img src="https://img.shields.io/github/stars/gnmyt/VoxelDash?style=flat-square&logo=github&color=f97316" alt="GitHub Stars"></a>
  <a href="https://github.com/gnmyt/VoxelDash/network/members"><img src="https://img.shields.io/github/forks/gnmyt/VoxelDash?style=flat-square&logo=github&color=f97316" alt="GitHub Forks"></a>
  <a href="https://github.com/gnmyt/VoxelDash/issues"><img src="https://img.shields.io/github/issues/gnmyt/VoxelDash?style=flat-square&logo=github" alt="GitHub Issues"></a>
  <a href="https://github.com/gnmyt/VoxelDash/releases/latest"><img src="https://img.shields.io/github/downloads/gnmyt/VoxelDash/total?style=flat-square&logo=github&color=f97316" alt="Downloads"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/gnmyt/VoxelDash?style=flat-square" alt="License"></a>
</p>
<p align="center">
  <a href="https://voxeldash.dev">📖 Documentation</a> · 
  <a href="https://github.com/gnmyt/VoxelDash/issues/new?template=bug_report.md">🐛 Report Bug</a> · 
  <a href="https://github.com/gnmyt/VoxelDash/issues/new?template=feature_request.md">✨ Request Feature</a>
</p>

---

## Features

VoxelDash provides everything you need to manage your Minecraft server from a sleek, modern web interface:

| Feature | Description |
|---------|-------------|
| **Dashboard** | Real-time server status, player count, memory usage, and TPS monitoring |
| **Player Management** | View online players, manage whitelist, and moderate your community |
| **File Manager** | Browse, edit, upload, and manage server files directly in your browser |
| **Console** | Full server console access with command execution |
| **World Management** | Manage multiple worlds, change settings, and organize your server |
| **Plugin Manager** | Install, update, and manage plugins with SpigotMC integration |
| **Backups** | Create and restore server backups with scheduling support |
| **Schedules** | Automate tasks with daily, weekly, or monthly schedules |
| **Configuration** | Edit server.properties and other configuration files easily |

## Screenshots

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src=".github/screenshots/overview.png" alt="Dashboard" width="400">
        <br><strong>Dashboard</strong>
      </td>
      <td align="center">
        <img src=".github/screenshots/players.png" alt="Players" width="400">
        <br><strong>Players</strong>
      </td>
    </tr>
    <tr>
      <td align="center">
        <img src=".github/screenshots/file_manager.png" alt="File Manager" width="400">
        <br><strong>File Manager</strong>
      </td>
      <td align="center">
        <img src=".github/screenshots/console.png" alt="Console" width="400">
        <br><strong>Console</strong>
      </td>
    </tr>
    <tr>
      <td align="center">
        <img src=".github/screenshots/plugins.png" alt="Plugins" width="400">
        <br><strong>Plugins</strong>
      </td>
      <td align="center">
        <img src=".github/screenshots/backups.png" alt="Backups" width="400">
        <br><strong>Backups</strong>
      </td>
    </tr>
  </table>
</div>

## Quick Start

### Requirements

- Java 17 or higher
- A Minecraft server (Spigot, Paper, Fabric, or Vanilla)

### Installation

1. **Download** the latest release from the [releases page](https://github.com/gnmyt/VoxelDash/releases/latest)

2. **Install** the plugin/mod on your server:
   - **Spigot/Paper**: Place the `.jar` file in the `plugins` folder
   - **Fabric**: Place the `.jar` file in the `mods` folder
   - **Vanilla**: Run the standalone `.jar` file

3. **Start** your server and access the dashboard at `http://localhost:7867`

4. **Login** with the credentials shown in the console on first start

For detailed installation instructions, check out our [documentation](https://voxeldash.dev/getting-started/introduction).

## Tech Stack

- **Frontend**: React, TypeScript, Vite, Tailwind CSS, shadcn/ui
- **Backend**: Java, integrated with Minecraft server APIs
- **Supported Platforms**: Spigot, Paper, Fabric, Vanilla

## Contributing

Contributions are welcome! Feel free to:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <sub>Built with ❤️ by <a href="https://gnm.dev">GNM</a> and contributors</sub>
</div>
