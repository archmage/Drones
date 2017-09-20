# Drones [![Build Status](https://travis-ci.org/archmage/drones.svg?branch=master)](https://travis-ci.org/archmage/drones)

Drones is a small and simple game: the premise is that you are a lone survivor on some desolate planet. At your disposal are a handful of multi-purpose drones; you must use them to traverse the inhospitable landscape, search for resources, construct bases, dispose of local hostiles, and eventually find a way off the planet.

# Design Principles

Drones was designed to be _view-agnostic_ - from a technological standpoint, "players" of Drones would have to construct their own method of interacting with the game library. This could take the form of a command-line interface (easier) or a graphical user interface (harder). 

The game's interactions are designed to be standardised through function calls. Regardless of presentation, everything _should_ behave identically.

# Status

Drones is currently _in progress_! A rewrite to use functional programming and unit testing is underway.