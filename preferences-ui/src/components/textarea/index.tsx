import { Component, ComponentChild, h } from "preact";
import styles from "./index.module.css";

interface TextareaProps {
  class?: string;
  value?: string;
  placeholder?: string;
  onChanged?: (content: string) => void;
}

export default class Textarea extends Component<TextareaProps> {
  state = {
    value: this.props.value,
  };

  onInput = (e: any) => {
    const { value } = e.target;
    this.setState({ value });
    this.props.onChanged?.call(null, value);
  };

  componentWillReceiveProps(props: TextareaProps) {
    if (props.value !== undefined) {
      this.state.value = props.value;
    }
  }

  render(): ComponentChild {
    return (
      <textarea
        class={`${this.props.class} ${styles.textarea}`}
        placeholder={this.props.placeholder}
        value={this.state.value}
        onInput={this.onInput}
      ></textarea>
    );
  }
}
