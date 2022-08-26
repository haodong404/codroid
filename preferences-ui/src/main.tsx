import { render, h } from "preact";
import "./index.css";
import { Preference } from "./preferences";

render(<Preference />, document.getElementById("app") as HTMLElement);
