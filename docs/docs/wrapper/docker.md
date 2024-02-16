# Docker Installation

If you plan on using Docker, you can install our pre-configured Docker image. This image contains all the necessary
dependencies to run the code in this repository.

## Installation

### Automatic Port Exposure

:::warning Important
Using this method is not recommended for production environments. However, if you want a convenient way to run the
code in this repository, you can use the following command to expose the necessary ports to the host machine.
:::

```bash
docker run -d -v mcdash:/app/data --restart=unless-stopped --network host --name MCDash germannewsmaker/mcdash
```

### Manual Port Exposure

If you want to manually expose the necessary ports to the host machine, you can use the following command.

:::tip Note
The port 7865 is used by the MCDash web interface. You still need to add the necessary port mappings for the
minecraft servers.

:::

```bash
docker run -d -v mcdash:/app/data --restart=unless-stopped -p 7865:7865 -p 25565:25565 --name MCDash germannewsmaker/mcdash
```


## Docker Compose

If you want to use Docker Compose, you can use the following `docker-compose.yml` file.

### Automatic Port Exposure

```yaml
version: '3.7'
services:
  mcdash:
    image: germannewsmaker/mcdash
    container_name: MCDash
    restart: unless-stopped
    volumes:
      - mcdash:/app/data
    network_mode: host
volumes:
    mcdash:
```

### Without Port Exposure

```yaml
version: '3.7'
services:
  mcdash:
    image: germannewsmaker/mcdash
    container_name: MCDash
    restart: unless-stopped
    volumes:
      - mcdash:/app/data
    ports:
      - 7865:7865
      - 25565:25565 # Add the necessary port mappings for the minecraft servers
volumes:
    mcdash:
```