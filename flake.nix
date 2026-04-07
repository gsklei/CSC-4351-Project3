{
  description = "Project Yurimech dev environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        packages.default = pkgs.godot-mono;

        apps.default = {
          type = "app";
          program = "${pkgs.godot-mono}/bin/godot-mono";
        };

        devShells.default = pkgs.mkShell {
          packages = with pkgs; [
            javaPackages.compiler.temurin-bin.jre-25

            vscodium
          ];
        };
      }
    );
}
