import { Component, ComponentChild, h } from "preact";
import Dropdown from "../dropdown";
import { DropdownSettingItem, Setting } from "./props";
import styles from "./index.module.css";

export default class DropdownSetting extends Component<DropdownSettingItem> {
  render(): ComponentChild {
    return (
      <div class={styles.setting_root}>
        <h1 class={styles.setting_title}>{this.props.title}</h1>
        <p class={styles.setting_subtitle}>{this.props.subtitle}</p>
        <div class="mt-2 flex justify-end">
          {h(Dropdown, {
            selected: this.props.value,
            items: this.props.items,
          })}
        </div>
      </div>
    );
  }
}
