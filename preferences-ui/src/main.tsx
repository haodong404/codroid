import { render, h } from "preact";
import "./index.css";
import "virtual:svg-icons-register";
import { Preference } from "./preferences";

render(<Preference />, document.getElementById("app") as HTMLElement);
