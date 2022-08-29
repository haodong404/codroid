import { Component, ComponentChild, h } from "preact";
import { DropdownSettingItem, Setting, SwitchSettingItem } from "./props";
import styles from "./index.module.css";
import Switch from "../../switch";

export default class SwitchSetting extends Component<SwitchSettingItem> {
  render(): ComponentChild {
    return (
      <div
        class={`flex gap-2 justify-between items-center ${styles.setting_root}`}
      >
        <div>
          <h1 class={styles.setting_title}>{this.props.title}</h1>
          <p class={styles.setting_subtitle}>{this.props.subtitle}</p>
        </div>
        <div>
          <Switch id={this.props.id} checked={this.props.defaultValue} />
        </div>
      </div>
    );
  }
}
