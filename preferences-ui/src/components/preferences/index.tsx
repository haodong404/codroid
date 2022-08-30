import { Component, ComponentChild, Fragment, h } from "preact";
import { Button } from "../button";
import DropdownSetting from "./setting-items/dropdown";
import styles from "./index.module.css";
import {
  Setting,
  SettingCategory,
  DropdownSettingItem,
  SwitchSettingItem,
  TextfieldSettingItem,
} from "./setting-items/props";
import SwitchSetting from "./setting-items/switch";
import TextfieldSetting from "./setting-items/textfield";
import TextareaSetting from "./setting-items/textarea";
import colors from "tailwindcss/colors";

export interface PreferencesProps {
  class?: string;
  title: string;
  settings: {};
}
export default class Preferences extends Component<PreferencesProps> {
  loadSettingItem = (setting: Setting): h.JSX.Element => {
    switch (setting.category) {
      case SettingCategory.Dropdown:
        return h(DropdownSetting, setting as DropdownSettingItem);
      case SettingCategory.Switch:
        return h(SwitchSetting, setting as SwitchSettingItem);
      case SettingCategory.Input:
        return h(TextfieldSetting, setting as TextfieldSettingItem);
      case SettingCategory.Textarea:
        return h(TextareaSetting, setting as TextfieldSettingItem);
      default:
        return <></>;
    }
  };

  resolveSettingItems = (): h.JSX.Element[] => {
    const result = Array<h.JSX.Element>();
    for (const [key, value] of Object.entries(this.props.settings)) {
      (value as Setting).id = key;
      result.push(this.loadSettingItem(value as Setting));
    }
    return result;
  };

  render(): ComponentChild {
    return (
      <>
        <section class={`${styles.settings_head} ${this.props.class}`}>
          <h1 class={styles.settings_title}>{this.props.title}</h1>
          <Button color={colors.red} text="RESET" />
        </section>
        <section class="flex flex-col divide-y">
          {this.resolveSettingItems()}
        </section>
      </>
    );
  }
}
