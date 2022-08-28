import { Component, ComponentChild, h } from "preact";
import styles from "./index.module.css";

interface TextfieldProps {
  class?: string;
  placeholder?: string;
  value?: string;
}

export default class Textfield extends Component<TextfieldProps> {
  render(): ComponentChild {
    return (
      <>
        <input
          class={`${this.props.class} ${styles.input}`}
          placeholder={this.props.placeholder}
          value={this.props.value}
        ></input>
      </>
    );
  }
}
