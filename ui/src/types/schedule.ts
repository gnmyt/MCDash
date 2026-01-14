export type ScheduleInterval = "HOURLY" | "DAILY" | "WEEKLY";

export type ActionInputType = "NONE" | "TEXT" | "TEXTAREA" | "NUMBER";

export interface ScheduleAction {
  id: string;
  translationKey: string;
  inputType: ActionInputType;
  inputTranslationKey?: string;
}

export interface ScheduleTask {
  id: number;
  scheduleId: number;
  actionId: string;
  metadata: string;
  executionOrder: number;
}

export interface Schedule {
  id: number;
  name: string;
  interval: ScheduleInterval;
  intervalValue: number;
  timeValue: number;
  enabled: boolean;
  lastRun: number;
  description: string;
  tasks: ScheduleTask[];
}

export interface CreateScheduleRequest {
  name: string;
  interval: ScheduleInterval;
  intervalValue: number;
  timeValue?: number;
}

export interface CreateTaskRequest {
  actionId: string;
  metadata?: string;
}

export interface UpdateTaskRequest {
  actionId: string;
  metadata?: string;
  executionOrder?: number;
}
