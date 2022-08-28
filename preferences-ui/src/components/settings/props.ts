export type Setting = DropdownSettingItem | SwitchSettingItem;

export enum SettingType {
  Input = "input",
  Dropdown = "select",
  Switch = "switch",
}

export interface SettingItem {
  type: SettingType;
  title: string;
  subtitle?: string;
}

export interface DropdownSettingItem extends SettingItem {
  value: number;
  items: Array<string>;
}

export interface SwitchSettingItem extends SettingItem {
  value: boolean;
}
