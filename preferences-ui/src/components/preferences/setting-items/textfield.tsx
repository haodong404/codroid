import { Component, ComponentChild, h } from "preact";
import { TextfieldSettingItem } from "./props";
import styles from "./index.module.css";
import Textfield from "../../textfield";

export default class TextfieldSetting extends Component<TextfieldSettingItem> {
  render(): ComponentChild {
    return (
      <div class={`${styles.setting_root}`}>
        <h1 class={styles.setting_title}>{this.props.title}</h1>
        <p class={styles.setting_subtitle}>{this.props.subtitle}</p>
        <div class="mt-2 flex justify-end">
          <Textfield
            class="w-2/3"
            value={this.props.defaultValue}
            placeholder={this.props.placeholder}
          />
        </div>
      </div>
    );
  }
}
