import { Man } from "./man.js";
import { Woman } from "./woman.js";

const alice = new Woman("Alice", "Wonder", new Date("1980-01-06"));
const bob = new Man("Bob", "Over", new Date("1988-05-12"));
const humans = [alice, bob];
const $list = document.querySelector("#humans-list");

humans.forEach(function(human) {
    const msg = human.sayHello();
    const $li = document.createElement("li");
    const $text = document.createTextNode(msg);

    $li.appendChild($text);
    $list.appendChild($li);
});