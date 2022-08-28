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

export interface SettingsProps {
  class?: string;
  title: string;
}

const preferences: Array<Setting> = [
  {
    type: SettingType.Dropdown,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: 0,
    items: [
      "Aaaa",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Auto",
      "a",
      "org.codroid.textmate.TextMate",
    ],
  },
  {
    type: SettingType.Dropdown,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: 3,
    items: [
      "Aaaa",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Hello World!",
      "Auto",
      "a",
      "org.codroid.textmate.TextMate",
    ],
  },
  {
    type: SettingType.Switch,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: false,
  },
  {
    type: SettingType.Input,
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
    value: "Default value",
    placeholder: "Please enter"
  },
];

export default class Preferences extends Component<SettingsProps> {
  loadSettingItem = (setting: Setting): h.JSX.Element => {
    switch (setting.type) {
      case SettingType.Dropdown:
        return h(DropdownSetting, setting as DropdownSettingItem);
      case SettingType.Switch:
        return h(SwitchSetting, setting as SwitchSettingItem);
      case SettingType.Input:
        return h(TextfieldSetting, setting as TextfieldSettingItem);
      default:
        return <></>;
    }
  };

  render(): ComponentChild {
    return (
      <>
        <section class={`${styles.settings_head} ${this.props.class}`}>
          <h1 class={styles.settings_title}>{this.props.title}</h1>
          <Button></Button>
        </section>
        <section class="flex flex-col divide-y">
          {preferences.map((it) => (
            <Fragment key={it}>{this.loadSettingItem(it)}</Fragment>
          ))}
        </section>
      </>
    );
  }
}
