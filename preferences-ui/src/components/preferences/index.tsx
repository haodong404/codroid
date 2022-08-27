import { Component, ComponentChild, Fragment, h } from "preact";
import { Button } from "../button";
import DropdownSetting from "../settings/dropdown";
import { SettingItem } from "../settings/props";
import styles from "./index.module.css";

export interface SettingsProps {
  class?: string;
  title: string;
}

const preferences: Array<SettingItem> = [
  {
    title: "Auto close Brackets",
    subtitle:
      "Controls whether the editor should remove adjacent closing quotes or brackets when deleting.",
  },
];

export default class Preferences extends Component<SettingsProps> {
  render(): ComponentChild {
    return (
      <>
        <section class={`${styles.settings_head} ${this.props.class}`}>
          <h1 class={styles.settings_title}>{this.props.title}</h1>
          <Button></Button>
        </section>
        {preferences.map((it) => (
          <Fragment key={it}>{h(DropdownSetting, it)}</Fragment>
        ))}
      </>
    );
  }
}
