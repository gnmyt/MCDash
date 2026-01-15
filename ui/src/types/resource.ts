export interface ResourceType {
  identifier: string;
  folderName: string;
}

export interface Resource {
  name: string;
  fileName: string;
  type: string;
  version: string | null;
  description: string | null;
  authors: string[];
  enabled: boolean;
  iconPath: string | null;
  fileSize: number;
}

export interface ConfigFile {
  name: string;
  path: string;
  size: number;
}

export type ConfigValue = string | number | boolean | null | ConfigValue[] | { [key: string]: ConfigValue };
