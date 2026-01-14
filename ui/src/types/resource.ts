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
