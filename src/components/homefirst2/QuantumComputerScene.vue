<template>
  <div ref="container" class="scene-container"></div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { RGBELoader } from 'three/addons/loaders/RGBELoader.js';

const container = ref(null);
let scene, camera, renderer, controls;
let cryostatGroup;
let animationId;
let clock;

// Colors
const COLORS = {
  gold: 0xC5A059,
  silver: 0xD1D5DB,
  copper: 0xB87333,
  chip: 0x111111
};

onMounted(() => {
  initScene();
  animate();
  window.addEventListener('resize', onWindowResize);
});

onBeforeUnmount(() => {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  window.removeEventListener('resize', onWindowResize);
  if (renderer) {
    renderer.dispose();
  }
});

function initScene() {
  if (!container.value) return;

  clock = new THREE.Clock();

  // Scene setup
  scene = new THREE.Scene();
  // scene.background = new THREE.Color(0xf5f4f0); // Match parent bg or transparent
  scene.background = null;

  // Camera
  camera = new THREE.PerspectiveCamera(45, container.value.clientWidth / container.value.clientHeight, 0.1, 1000);
  camera.position.set(0, 0, 4.5);

  // Renderer
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.toneMapping = THREE.ACESFilmicToneMapping;
  renderer.toneMappingExposure = 1;
  container.value.appendChild(renderer.domElement);

  // Controls
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.enableZoom = false;

  // Lighting
  const ambientLight = new THREE.AmbientLight(0xffffff, 1);
  scene.add(ambientLight);

  const spotLight = new THREE.SpotLight(COLORS.gold, 2);
  spotLight.position.set(5, 5, 5);
  spotLight.angle = 0.3;
  spotLight.penumbra = 1;
  scene.add(spotLight);

  const pointLight = new THREE.PointLight(0xffffff, 0.5);
  pointLight.position.set(-5, -5, -5);
  scene.add(pointLight);

  // Environment Map (Simulated Studio)
  // Since we don't have the exact env map, we'll use a simple one or just rely on lights + metalness
  // For better metal look, let's add a simple cube map or just good lighting

  // Cryostat Group
  cryostatGroup = new THREE.Group();
  cryostatGroup.position.set(0, 0.5, 0);
  scene.add(cryostatGroup);

  createCryostat();
}

function createCryostat() {
  const goldMaterial = new THREE.MeshStandardMaterial({
    color: COLORS.gold,
    metalness: 1,
    roughness: 0.15
  });

  const silverMaterial = new THREE.MeshStandardMaterial({
    color: COLORS.silver,
    metalness: 0.8,
    roughness: 0.2
  });

  const copperMaterial = new THREE.MeshStandardMaterial({
    color: COLORS.copper,
    metalness: 0.8,
    roughness: 0.3
  });

  const chipMaterial = new THREE.MeshStandardMaterial({
    color: COLORS.chip,
    metalness: 0.9,
    roughness: 0.1
  });

  // Top Plate
  const topPlate = new THREE.Mesh(new THREE.CylinderGeometry(1.2, 1.2, 0.1, 64), goldMaterial);
  topPlate.position.set(0, 1, 0);
  cryostatGroup.add(topPlate);

  // Middle Stage
  const middleStage = new THREE.Mesh(new THREE.CylinderGeometry(1, 1, 0.1, 64), goldMaterial);
  middleStage.position.set(0, 0.2, 0);
  cryostatGroup.add(middleStage);

  // Bottom Stage (Mixing Chamber)
  const bottomStage = new THREE.Mesh(new THREE.CylinderGeometry(0.6, 0.6, 0.1, 64), goldMaterial);
  bottomStage.position.set(0, -0.6, 0);
  cryostatGroup.add(bottomStage);

  // Connecting Rods (Upper)
  const rodGeo = new THREE.CylinderGeometry(0.04, 0.04, 0.8, 16);
  const rodPositions = [
    [0.5, 0.6, 0], [-0.5, 0.6, 0], [0, 0.6, 0.5], [0, 0.6, -0.5]
  ];
  rodPositions.forEach(pos => {
    const rod = new THREE.Mesh(rodGeo, silverMaterial);
    rod.position.set(...pos);
    cryostatGroup.add(rod);
  });

  // Connecting Rods (Lower)
  const lowerRodGeo = new THREE.CylinderGeometry(0.03, 0.03, 0.8, 16);
  const lowerRodPositions = [
    [0.2, -0.2, 0], [-0.2, -0.2, 0]
  ];
  lowerRodPositions.forEach(pos => {
    const rod = new THREE.Mesh(lowerRodGeo, silverMaterial);
    rod.position.set(...pos);
    cryostatGroup.add(rod);
  });

  // Coils/Wires
  const coil1 = new THREE.Mesh(new THREE.TorusGeometry(0.7, 0.015, 16, 64), copperMaterial);
  coil1.position.set(0, -0.2, 0);
  coil1.rotation.x = Math.PI / 2;
  cryostatGroup.add(coil1);

  const coil2 = new THREE.Mesh(new THREE.TorusGeometry(0.3, 0.015, 16, 64), copperMaterial);
  coil2.position.set(0, -1, 0);
  coil2.rotation.x = Math.PI / 2;
  cryostatGroup.add(coil2);

  // Chip
  const chip = new THREE.Mesh(new THREE.BoxGeometry(0.2, 0.05, 0.2), chipMaterial);
  chip.position.set(0, -0.7, 0);
  cryostatGroup.add(chip);
}

function animate() {
  animationId = requestAnimationFrame(animate);
  const t = clock.getElapsedTime();

  // Float animation
  if (cryostatGroup) {
    cryostatGroup.position.y = 0.5 + Math.sin(t) * 0.1; // Float up and down
    cryostatGroup.rotation.x = Math.sin(t * 0.5) * 0.05; // Gentle tilt
    cryostatGroup.rotation.z = Math.sin(t * 0.3) * 0.05; // Gentle tilt
    cryostatGroup.rotation.y = t * 0.2; // Slow rotation
  }

  if (controls) controls.update();
  if (renderer && scene && camera) renderer.render(scene, camera);
}

function onWindowResize() {
  if (!container.value || !camera || !renderer) return;

  camera.aspect = container.value.clientWidth / container.value.clientHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
}
</script>

<style scoped>
.scene-container {
  width: 100%;
  height: 100%;
  min-height: 300px;
}
</style>
