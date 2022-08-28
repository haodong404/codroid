import { Component, ComponentChild, h } from "preact";
import styles from "./index.module.css";

interface TextareaProps {
  class?: string;
  value?: string;
  placeholder?: string;
}

export default class Textarea extends Component<TextareaProps> {
  render(): ComponentChild {
    return (
      <textarea
        class={`${this.props.class} ${styles.textarea}`}
        placeholder={this.props.value}
        value={this.props.value}
      ></textarea>
    );
  }
}
