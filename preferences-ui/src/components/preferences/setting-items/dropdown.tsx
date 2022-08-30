import { Component, ComponentChild, h } from "preact";
import Dropdown from "../../dropdown";
import { DropdownSettingItem, Setting } from "./props";
import styles from "./index.module.css";

export default class DropdownSetting extends Component<DropdownSettingItem> {
  render(): ComponentChild {
    let defaultValue = 0;
    if (import.meta.env.PROD) {
      defaultValue = PreferencesInjection.getInt(this.props.id);
    } else if (this.props.defaultValue !== undefined) {
      defaultValue = this.props.defaultValue;
    }

    return (
      <div class={styles.setting_root}>
        <h1 class={styles.setting_title}>{this.props.title}</h1>
        <p class={styles.setting_summary}>{this.props.summary}</p>
        <div class="mt-2 flex justify-end">
          {h(Dropdown, {
            defaultValue: defaultValue,
            options: this.props.options,
            onSelected: (index: number) => {
              if (import.meta.env.PROD) {
                PreferencesInjection.putInt(this.props.id, index);
              }
            },
          })}
        </div>
      </div>
    );
  }
}
