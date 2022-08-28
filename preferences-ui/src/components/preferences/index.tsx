import { Component, ComponentChild, Fragment, h } from "preact";
import { Button } from "../button";
import DropdownSetting from "./setting-items/dropdown";
import styles from "./index.module.css";
import { Setting, SettingType, DropdownSettingItem, SwitchSettingItem } from "./setting-items/props";
import SwitchSetting from "./setting-items/switch";

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
    value: true,
  },
];

export default class Preferences extends Component<SettingsProps> {
  loadSettingItem = (setting: Setting): h.JSX.Element => {
    switch (setting.type) {
      case SettingType.Dropdown:
        return h(DropdownSetting, setting as DropdownSettingItem);
        case SettingType.Switch:
          return h(SwitchSetting, setting as SwitchSettingItem)
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
