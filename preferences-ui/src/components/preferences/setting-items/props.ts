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
  id: string;
  category: SettingType;
  title: string;
  subtitle?: string;
}

export interface DropdownSettingItem extends SettingItem {
  defaultValue: number;
  options: Array<string>;
}

export interface SwitchSettingItem extends SettingItem {
  defaultValue: boolean;
}

export interface TextfieldSettingItem extends SettingItem {
  defaultValue?: string;
  placeholder?: string;
}

export interface TextareaSettingItem extends TextfieldSettingItem {}
