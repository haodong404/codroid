export type Setting =
  | DropdownSettingItem
  | SwitchSettingItem
  | TextfieldSettingItem;

export enum SettingType {
  Input = "input",
  Dropdown = "select",
  Switch = "switch",
  Textarea = "textarea",
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

export interface TextfieldSettingItem extends SettingItem {
  value?: string;
  placeholder?: string;
}

export interface TextareaSettingItem extends TextfieldSettingItem {}
