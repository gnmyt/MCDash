export interface OnlinePlayer {
  name: string;
  uuid: string;
  world: string;
  ipAddress: string;
  health: number;
  hunger: number;
  op: boolean;
  gamemode: string;
  playtime: number;
}

export interface OfflinePlayer {
  name: string;
  uuid: string;
}

export interface BannedPlayer {
  name: string;
  uuid: string;
  reason: string | null;
  banDate: number | null;
  expiry: number | null;
  source: string | null;
}

export interface WhitelistData {
  players: OfflinePlayer[];
  enabled: boolean;
}
