#!/bin/bash
if (( $# == 0 )); then
	./gradlew -q --console=plain run
else
	./gradlew -q --console=plain run --args="$*"
fi