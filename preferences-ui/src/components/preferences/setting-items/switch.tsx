import { Component, ComponentChild, h } from "preact";
import { DropdownSettingItem, Setting, SwitchSettingItem } from "./props";
import styles from "./index.module.css";
import Switch from "../../switch";

export default class SwitchSetting extends Component<SwitchSettingItem> {
  state = {
    checked: false,
  };

  render(): ComponentChild {
    this.state.checked = false;
    if (import.meta.env.PROD) {
      this.state.checked = PreferencesInjection.getBoolean(this.props.id);
    } else if (this.props.defaultValue !== undefined) {
      this.state.checked = this.props.defaultValue;
    }
    return (
      <label
        for={this.props.id}
        class={`flex gap-2 justify-between items-center ${styles.setting_root}`}
      >
        <div>
          <h1 class={styles.setting_title}>{this.props.title}</h1>
          <p class={styles.setting_summary}>{this.props.summary}</p>
        </div>
        <div>
          <Switch
            id={this.props.id}
            checked={this.state.checked}
            onChanged={(checked: boolean) => {
              if (import.meta.env.PROD) {
                PreferencesInjection.putBoolean(this.props.id, checked);
              }
            }}
          />
        </div>
      </label>
    );
  }
}
