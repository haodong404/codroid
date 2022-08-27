export interface SettingItem {
  title: string;
  subtitle?: string;
}

export interface DropdownItem extends SettingItem {
  value: string;
  list: Array<string>;
}
