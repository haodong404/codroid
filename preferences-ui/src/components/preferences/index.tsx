import { Component, ComponentChild, Fragment, h } from "preact";
import { Button } from "../button";
import DropdownSetting from "./setting-items/dropdown";
import styles from "./index.module.css";
import {
  Setting,
  SettingType,
  DropdownSettingItem,
  SwitchSettingItem,
  TextfieldSettingItem,
} from "./setting-items/props";
import SwitchSetting from "./setting-items/switch";
import TextfieldSetting from "./setting-items/textfield";
import TextareaSetting from "./setting-items/textarea";
import colors from "tailwindcss/colors";

export interface SettingsProps {
  class?: string;
  title: string;
  settings: Array<Setting>;
}
export default class Preferences extends Component<SettingsProps> {
  loadSettingItem = (setting: Setting): h.JSX.Element => {
    switch (setting.type) {
      case SettingType.Dropdown:
        return h(DropdownSetting, setting as DropdownSettingItem);
      case SettingType.Switch:
        return h(SwitchSetting, setting as SwitchSettingItem);
      case SettingType.Input:
        return h(TextfieldSetting, setting as TextfieldSettingItem);
      case SettingType.Textarea:
        return h(TextareaSetting, setting as TextfieldSettingItem);
      default:
        return <></>;
    }
  };

  render(): ComponentChild {
    return (
      <>
        <section class={`${styles.settings_head} ${this.props.class}`}>
          <h1 class={styles.settings_title}>{this.props.title}</h1>
          <Button color={colors.red} text="RESET" />
        </section>
        <section class="flex flex-col divide-y">
          {this.props.settings.map((it) => (
            <Fragment key={it}>{this.loadSettingItem(it)}</Fragment>
          ))}
        </section>
      </>
    );
  }
}
