import { Component, ComponentChild, h } from "preact";

export interface SwitchProps {
  id: string;
  checked?: boolean;
  onChanged?: (checked: boolean) => void;
}

export default class Switch extends Component<SwitchProps> {
  state = {
    checked: this.props.checked,
  };

  toggle = () => {
    let checked = !this.state.checked;
    this.setState({ checked: checked });
    this.props.onChanged?.call(null, checked);
  };

  componentWillReceiveProps(props: SwitchProps): boolean {
    this.state.checked = props.checked;
    return true;
  }

  render(): ComponentChild {
    return (
      <>
        <label
          for={this.props.id}
          class="inline-flex relative items-center cursor-pointer"
        >
          <input
            type="checkbox"
            checked={this.state.checked}
            id={this.props.id}
            onClick={this.toggle}
            class="sr-only peer"
          />
          <div
            class="w-11 h-6 bg-primary-50 border-[1.5px] border-primary-900 transition-all
            outline-none
          peer-focus:outline-none
          rounded-full peer peer-checked:after:translate-x-full
           after:content-[''] 
           after:absolute after:top-[0.35rem] after:left-[0.35rem] 
           after:bg-primary-900
            after:rounded-full after:h-3 after:w-3 after:transition-all
              peer-checked:bg-primary-800
               peer-checked:after:h-5  peer-checked:after:w-5 peer-checked:after:top-[0.13rem] peer-checked:after:left-[0.1rem]
               peer-checked:border-none peer-checked:after:bg-white"
          ></div>
        </label>
      </>
    );
  }
}
