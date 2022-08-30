import { Component, ComponentChild, h } from "preact";
import { TextareaSettingItem } from "./props";
import styles from "./index.module.css";
import Textarea from "../../textarea";

export default class TextareaSetting extends Component<TextareaSettingItem> {
  render(): ComponentChild {
    return (
      <div class={`${styles.setting_root}`}>
        <h1 class={styles.setting_title}>{this.props.title}</h1>
        <p class={styles.setting_summary}>{this.props.summary}</p>
        <div class="mt-2 flex justify-end">
          <Textarea
            class="w-2/3"
            value={this.props.defaultValue}
            placeholder={this.props.placeholder}
          />
        </div>
      </div>
    );
  }
}
