export type BackupType = "ROOT" | "PLUGINS" | "CONFIGS" | "LOGS"

export interface Backup {
  id: number
  size: number
  modes: BackupType[]
}